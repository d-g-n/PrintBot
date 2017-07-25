

import com.github.decyg.command.CommandCore
import com.github.decyg.core.AuditLog
import com.github.decyg.core.indicateSuccess
import com.github.decyg.core.sendConfirmationEmbed
import com.github.decyg.tokenizer.TextToken
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("pardon")
    prettyName = "Pardon"
    description = "Unbans people"
    requiredPermission = Permissions.BAN
    argumentParams = listOf(
            TextToken("the ID of a user to unban") isOptional false isGreedy false
    )
    behaviour = { event, tokens ->

        val userID = tokens[0] as TextToken

        val pardonBool = event.channel.sendConfirmationEmbed(
                event.author,
                bodyMessage = "Are you really sure you want to pardon user with id ${userID.underlyingString}"
        )

        if(pardonBool) {
            event.guild.pardonUser(userID.underlyingString)

            AuditLog.log(
                    event.guild,
                    "User ${event.author} (${event.author.stringID}) has pardoned user with id ${userID.underlyingString}"
            )

            event.message.indicateSuccess()
        }

    }
}

