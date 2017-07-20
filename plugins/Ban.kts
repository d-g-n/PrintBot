
import com.github.decyg.command.CommandCore
import com.github.decyg.core.sendMessage
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.UserToken

CommandCore.command {
    commandAliases = listOf("ban")
    prettyName = "Ban"
    description = "Bans people"
    argumentParams = listOf(
            UserToken() isOptional false isGreedy false,
            TextToken() isOptional true isGreedy true
    )
    behaviour = { event, tokens ->
        event.sendMessage("go away")
    }
}

