
import com.github.decyg.command.CommandCore
import com.github.decyg.core.DiscordCore
import com.github.decyg.core.getUserResponse
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("auditlog")
    prettyName = "Audit Log Channel"
    description = "brings up a dialogue to change the audit log channel"
    requiredPermission = Permissions.ADMINISTRATOR
    argumentParams = listOf()
    behaviour = { event, _ ->

        val newChannel = event.channel.getUserResponse(
                event.author,
                header = "Audit Log ID",
                bodyMessage = "Currently the audit channel id is set to `${DiscordCore.guildConfigStore[event.guild.id]!!.auditLogChannelID}`\n" +
                        "Please enter the ID of the channel you wish to use as an audit log"
        ) ?: DiscordCore.guildConfigStore[event.guild.id]!!.auditLogChannelID

        val existingConfig = DiscordCore.guildConfigStore[event.guild.id]!!

        existingConfig.auditLogChannelID = newChannel

        DiscordCore.updateGuildConfigStore(event.guild.id, existingConfig)

    }
}

