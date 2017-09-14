
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.decyg.command.CommandCore
import com.github.decyg.command.CommandStore
import com.github.decyg.core.*
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.UserToken
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RequestBuffer

data class CCommandPOKO(var commandMap : MutableMap<String, String> = mutableMapOf())

CommandCore.command {
    commandAliases = listOf("customcommand", "cc")
    prettyName = "Custom Command"
    description = "Creates a custom command that returns text"
    requiredPermission = Permissions.MANAGE_CHANNEL
    argumentParams = listOf(
            TextToken("custom command name") isOptional true isGreedy false,
            TextToken("custom command contents") isOptional true isGreedy true
    )
    behaviour = end@ { event, tokens ->

        if(tokens.isEmpty()){

            var pluginList = ""

            val poko = DiscordCore.getConfigForGuild(event.guild)
            if (poko.pluginSettings.containsKey(this.prettyName)) {

                val curPOKO = DiscordCore.mapper.readValue<CCommandPOKO>(poko.pluginSettings[this.prettyName]!!)

                pluginList = curPOKO.commandMap.map { it.key }.joinToString()

            }
            event.channel.sendInfoEmbed(
                    header = "List of custom commands",
                    bodyMessage = pluginList
            )
            return@end
        }

        val commandName = (tokens[0] as TextToken).underlyingString
        val commandContents = tokens.asMessageString(0)

        if(CommandStore.commandStore.containsKey(commandName))
            return@end

        DiscordCore.accessPluginSettingsWithPOKO(event.guild, this.prettyName) { curPOKO: CCommandPOKO ->

            if(commandContents == "") { // delete

                if(curPOKO.commandMap.containsKey(commandName)){

                    curPOKO.commandMap.remove(commandName)

                    event.channel.sendInfoEmbed(
                            bodyMessage =
                            "Successfully removed custom command `$commandName`"
                    )

                } else {

                    event.channel.sendErrorEmbed(
                            errorMessage = "Cannot delete a command that isn't registered "
                    )

                }

            } else { // upsert

                curPOKO.commandMap.put(commandName, commandContents)

                event.channel.sendInfoEmbed(
                        bodyMessage =
                        "Successfully registered custom command `$commandName` with contents <$commandContents>"
                )

            }

        }

        event.message.indicateSuccess()

    }
    passiveListener = end@ { event ->
        if (event !is MessageReceivedEvent)
            return@end

        var msgString = event.message.content

        if(msgString.startsWith('!')){
            msgString = msgString.drop(1).trim()
        } else {
            return@end
        }

        val poko = DiscordCore.getConfigForGuild(event.guild)
        if (poko.pluginSettings.containsKey(this.prettyName)) {

            val curPOKO = DiscordCore.mapper.readValue<CCommandPOKO>(poko.pluginSettings[this.prettyName]!!)

            if(curPOKO.commandMap.containsKey(msgString)){
                event.channel.sendBufferedMessage(curPOKO.commandMap[msgString]!!, 0)
            }

        }

    }
}