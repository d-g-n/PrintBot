

import com.github.decyg.command.CommandCore
import com.github.decyg.core.AuditLog
import com.github.decyg.core.asMessageString
import com.github.decyg.core.sendConfirmationEmbed
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.UserToken
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("kick")
    prettyName = "Kick"
    description = "Kicks people"
    requiredPermission = Permissions.KICK
    argumentParams = listOf(
            UserToken("a user to kick") isOptional false isGreedy false,
            TextToken("a reason") isOptional false isGreedy true
    )
    behaviour = { event, tokens ->

        val user = tokens[0] as UserToken
        val reason = tokens.asMessageString(0)


        val kickBool = event.channel.sendConfirmationEmbed(
                event.author,
                bodyMessage = "Are you really sure you want to kick ${user.mentionedUser?.name ?: ""} (${user.mentionedUser!!.stringID})" +
                        " for the reason `$reason`"
        )

        if(kickBool) {
            event.guild.kickUser(user.mentionedUser, reason)

            AuditLog.log(
                    event.guild,
                    "User ${event.author} (${event.author.stringID}) has kicked user ${user.mentionedUser?.name ?: ""} (${user.mentionedUser!!.stringID})" +
                            " for the reason `$reason`"
            )
        }

    }
}

