
import com.github.decyg.command.CommandCore
import com.github.decyg.core.*
import com.github.decyg.permissions.PermissionPOKO
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

        val newPrefix = ownerDM.getUserResponse(event.guild.owner) ?: DiscordCore.guildConfigStore[event.guild.id]!!.serverPrefix

        ownerDM.sendInfoEmbed(
                "Role List",
                event.guild.roles.fold(""){ total, next ->
                    total + "$next : ${next.longID}\n"
                },
                secondsTimeout = 180
        )

        ownerDM.sendBufferedMessage("The above list represents every role on your server and the IDs they have.")

        ownerDM.sendBufferedMessage("Now copy and paste the IDs of the roles you want to be recognised as TRUSTED, " +
                "you can enter more than one role by separating them by a space like so `23232 232323`.")

        val trustedRoles = ownerDM.getUserResponse(event.guild.owner, secondsTimeout = 60)?.split(" ") ?: emptyList()

        ownerDM.sendBufferedMessage("Now copy and paste the IDs of the role you want to be recognised as MODERATOR")

        val moderatorRoles = ownerDM.getUserResponse(event.guild.owner, secondsTimeout = 60)?.split(" ") ?: emptyList()

        ownerDM.sendBufferedMessage("Now copy and paste the IDs of the role you want to be recognised as ADMINISTRATOR")

        val administratorRoles = ownerDM.getUserResponse(event.guild.owner, secondsTimeout = 60)?.split(" ") ?: emptyList()

        val confirmationBool = ownerDM.sendConfirmationEmbed(
                event.guild.owner,
                "Are you sure?",
                "Please read over the following and ensure that they are the values you expected:\n" +
                        "Prefix: `$newPrefix`\n" +
                        "Trusted IDs: `$trustedRoles`\n" +
                        "Moderator IDs: `$moderatorRoles`\n" +
                        "Administrator IDs: `$administratorRoles`\n" +
                        "If they are please react Y, reacting N or letting it time out will result in NO changes being made",
                60
        )

        if(confirmationBool){

            val newPerms = PermissionPOKO(
                    newPrefix,
                    trustedRoles,
                    moderatorRoles,
                    administratorRoles
            )

            DiscordCore.updateGuildConfigStore(event.guild.id, newPerms)

            ownerDM.sendBufferedMessage("Your changes have been applied. Remember, this command can be reran at any times if it needs to be configured again.")

        }

    }
}

