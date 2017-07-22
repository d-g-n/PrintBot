
import com.github.decyg.command.CommandCore
import com.github.decyg.core.DiscordCore
import com.github.decyg.core.getUserResponse
import com.github.decyg.core.sendBufferedMessage
import com.github.decyg.core.sendInfoEmbed
import com.github.decyg.permissions.RoleLevel

CommandCore.command {
    commandAliases = listOf("initialsetup")
    prettyName = "Initial Setup"
    description = "Runs the initial setup dialogue again"
    requiredPermission = RoleLevel.OWNER
    argumentParams = listOf()
    behaviour = { event, _ ->

        val ownerDM = event.guild.owner.orCreatePMChannel

        ownerDM.sendBufferedMessage(
                "Hi there! It seems like this is the first time I've joined your server, follow these next steps to set up the roles and prefix correctly.\n" +
                        "The bot needs to have a role with required permissions for banning, kicking and role management.\n"
        )

        ownerDM.sendBufferedMessage("First, let's set up the prefix, currently the prefix is set to `${DiscordCore.guildConfigStore[event.guild.id]!!.serverPrefix}`\n" +
                "Please enter the prefix you wish to use, if you do not wish to change just enter the existing one.")

        val newPrefix = ownerDM.getUserResponse(event.guild.owner).trim()

        ownerDM.sendInfoEmbed(
                "Role List",
                event.guild.roles.fold(""){ total, next ->
                    total + "$next : ${next.longID}\n"
                }
        )

        ownerDM.sendBufferedMessage("The above list represents every role on your server and the IDs they have")
    }
}

