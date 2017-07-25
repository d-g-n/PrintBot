

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.decyg.command.CommandCore
import com.github.decyg.core.DiscordCore
import com.github.decyg.core.indicateSuccess
import com.github.decyg.core.sendErrorEmbed
import com.github.decyg.core.sendInfoEmbed
import com.github.decyg.tokenizer.TextToken
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions

data class PollPOKO(var pollChannels : MutableList<String>)

CommandCore.command {
    commandAliases = listOf("poll")
    prettyName = "Poll"
    description = "Designates specific channels as poll channels "
    requiredPermission = Permissions.MANAGE_CHANNEL
    argumentParams = listOf(
            TextToken("an id of a channel to toggle as a polling channel") isOptional false isGreedy false
    )
    behaviour = end@{ event, tokens ->

        val id = tokens[0] as TextToken

        if(event.guild.getChannelByID(id.underlyingString) == null) {
            event.channel.sendErrorEmbed(errorMessage = "That ID does not represent a channel")
            return@end
        }


        val poko = DiscordCore.getConfigForGuild(event.guild)
        if(!poko.pluginSettings.containsKey(this.prettyName)) {

            poko.pluginSettings[this.prettyName] = DiscordCore.mapper.writeValueAsString(PollPOKO(mutableListOf()))

        }

        val curPOKO = DiscordCore.mapper.readValue<PollPOKO>(poko.pluginSettings[this.prettyName]!!)

        if(curPOKO.pollChannels.contains(id.underlyingString)){
            curPOKO.pollChannels.remove(id.underlyingString)

            event.channel.sendInfoEmbed(bodyMessage = "The channel has been toggled off.")
        } else {
            curPOKO.pollChannels.add(id.underlyingString)

            event.channel.sendInfoEmbed(bodyMessage = "The channel has been toggled on.")
        }

        poko.pluginSettings[this.prettyName] = DiscordCore.mapper.writeValueAsString(curPOKO)

        DiscordCore.updateGuildConfigStore(event.guild.stringID)

        event.message.indicateSuccess()

    }
    passiveListener = end@{ event ->
        if(event !is MessageReceivedEvent)
            return@end

        // use this to initalise the config and save it back

        println(event)

    }
}

