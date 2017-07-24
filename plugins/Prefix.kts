
import com.github.decyg.command.CommandCore
import com.github.decyg.config.ConfigPOKO
import com.github.decyg.core.DiscordCore
import com.github.decyg.core.getUserResponse
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("prefix")
    prettyName = "Change Prefix"
    description = "brings up a dialogue to change the prefix"
    requiredPermission = Permissions.ADMINISTRATOR
    argumentParams = listOf()
    behaviour = { event, _ ->

        val newPrefix = event.channel.getUserResponse(
                event.author,
                header = "Prefix Config",
                bodyMessage = "Currently the prefix is set to `${DiscordCore.guildConfigStore[event.guild.id]!!.serverPrefix}`\n" +
                        "Please enter the prefix you wish to use, if you do not wish to change just enter the existing one.\n" +
                        "Note that you can use a mention for the bot as the prefix\n"
        ) ?: DiscordCore.guildConfigStore[event.guild.id]!!.serverPrefix

        val existingConfig = DiscordCore.guildConfigStore[event.guild.id]!!

        existingConfig.serverPrefix = newPrefix

        DiscordCore.updateGuildConfigStore(event.guild.id, existingConfig)

    }
}

