
import com.github.decyg.command.CommandCore
import com.github.decyg.core.*
import com.github.decyg.permissions.PermissionPOKO
import com.github.decyg.permissions.RoleLevel

CommandCore.command {
    commandAliases = listOf("initialsetup")
    prettyName = "Initial Setup"
    description = "Runs the initial setup dialogue to configure prefix and bound roles"
    requiredPermission = RoleLevel.OWNER
    argumentParams = listOf()
    behaviour = { event, _ ->

        val ownerDM = event.guild.owner.orCreatePMChannel

        val newPrefix = ownerDM.getUserResponse(
                event.guild.owner,
                header = "Prefix Config",
                bodyMessage = "Currently the prefix is set to `${DiscordCore.guildConfigStore[event.guild.id]!!.serverPrefix}`\n" +
                        "Please enter the prefix you wish to use, if you do not wish to change just enter the existing one.\n" +
                        "Note that you can use a mention for the bot as the prefix\n"
        ) ?: DiscordCore.guildConfigStore[event.guild.id]!!.serverPrefix

        ownerDM.sendInfoEmbed(
                "Role List",
                event.guild.roles.fold(""){ total, next ->
                    total + "$next : ${next.longID}\n"
                },
                secondsTimeout = 180
        )

        val trustedRoles = ownerDM.getUserResponse(
                event.guild.owner,
                secondsTimeout = 60,
                header = "Trusted Roles",
                bodyMessage = "Now copy and paste the IDs of the roles you want to be recognised as `TRUSTED`,\n" +
                        "you can enter more than one role by separating them by a space like so `23232 232323`."
        )?.split(" ") ?: emptyList()

        val moderatorRoles = ownerDM.getUserResponse(
                event.guild.owner,
                secondsTimeout = 60,
                header = "Moderator Roles",
                bodyMessage = "Now copy and paste the IDs of the roles you want to be recognised as `MODERATOR`"
        )?.split(" ") ?: emptyList()

        val administratorRoles = ownerDM.getUserResponse(
                event.guild.owner,
                secondsTimeout = 60,
                header = "Administrator Roles",
                bodyMessage = "Now copy and paste the IDs of the roles you want to be recognised as `ADMINISTRATOR`"
        )?.split(" ") ?: emptyList()

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

            ownerDM.sendInfoEmbed(
                    header = "success",
                    bodyMessage = "Your changes have been applied. Remember, this command can be reran at any times if it needs to be configured again."
            )
        }

    }
}

