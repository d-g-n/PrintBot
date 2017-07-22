
import com.github.decyg.command.CommandCore
import com.github.decyg.core.asMessageString
import com.github.decyg.core.sendBufferedMessage
import com.github.decyg.core.sendConfirmationEmbed
import com.github.decyg.permissions.RoleLevel
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.TimeToken
import com.github.decyg.tokenizer.UserToken

CommandCore.command {
    commandAliases = listOf("ban")
    prettyName = "Ban"
    description = "Bans people"
    requiredPermission = RoleLevel.MODERATOR
    argumentParams = listOf(
            UserToken("a user to ban") isOptional false isGreedy false,
            TimeToken("how long for, 0 for perma") isOptional false isGreedy false,
            TextToken("reason") isOptional true isGreedy true
    )
    behaviour = { event, tokens ->

        val user = tokens[0] as UserToken
        val length = tokens[1] as TimeToken
        val reason = tokens.asMessageString(1)


        val banBool = event.channel.sendConfirmationEmbed(
                event.author,
                bodyMessage = "Are you really sure you want to ban ${user.mentionedUser?.name ?: ""}"
        )
    }
}

