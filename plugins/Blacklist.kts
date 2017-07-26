

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.decyg.command.CommandCore
import com.github.decyg.core.AuditLog
import com.github.decyg.core.DiscordCore
import com.github.decyg.core.indicateSuccess
import com.github.decyg.core.sendInfoEmbed
import com.github.decyg.tokenizer.TextToken
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RequestBuffer

data class BlackListPOKO(var badWordList: MutableList<String>)


CommandCore.command {
    commandAliases = listOf("blacklist")
    prettyName = "Blacklist"
    description = "Blacklists a range of words, will autodelete the message if the word is found and report the deletion to the audit log"
    requiredPermission = Permissions.MANAGE_CHANNELS
    argumentParams = listOf(
            TextToken("word to add/remove from the blacklist") isOptional true isGreedy false
    )
    behaviour = end@{ event, tokens ->

        DiscordCore.accessPluginSettingsWithPOKO(event.guild, this.prettyName) { curPOKO: BlackListPOKO ->

            if (tokens.isEmpty()) {

                event.channel.sendInfoEmbed(
                        header = "Blacklisted words",
                        bodyMessage = curPOKO.badWordList.fold("", { init, curObj ->
                            init +
                                    "```\n" +
                                    curObj +
                                    "```\n"
                        })
                )

            } else {

                val badWord = (tokens[0] as TextToken).underlyingString

                if(curPOKO.badWordList.contains(badWord)){
                    curPOKO.badWordList.remove(badWord)

                    event.channel.sendInfoEmbed(bodyMessage = "That word has been removed from the list of bad words.")
                } else {
                    curPOKO.badWordList.add(badWord)

                    event.channel.sendInfoEmbed(bodyMessage = "That word has been added from the list of bad words.")
                }

            }

        }

        event.message.indicateSuccess()

    }
    passiveListener = end@{ event ->
        if(event !is MessageReceivedEvent)
            return@end

        val poko = DiscordCore.getConfigForGuild(event.guild)

        if(!poko.pluginSettings.containsKey(this.prettyName))
            return@end

        val curPOKO = DiscordCore.mapper.readValue<BlackListPOKO>(poko.pluginSettings[this.prettyName]!!)

        curPOKO.badWordList.forEach {
            if(event.message.content.toLowerCase().contains(it)){

                RequestBuffer.request { event.message.delete() }

                AuditLog.log(
                        event.guild,
                        "The message `${event.message.content}` by ${event.author} (${event.author.stringID}) was deleted as it contained the badword `$it`"
                )

                return@end
            }
        }
    }
}

