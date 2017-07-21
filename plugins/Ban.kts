
import com.github.decyg.command.CommandCore
import com.github.decyg.core.sendBufferedMessage
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.UserToken

CommandCore.command {
    commandAliases = listOf("ban")
    prettyName = "Ban"
    description = "Bans people"
    argumentParams = listOf(
            UserToken("a user to ban") isOptional false isGreedy false,
            TextToken("reason") isOptional true isGreedy true
    )
    behaviour = { event, tokens ->
        event.sendBufferedMessage("go away")
    }
}

