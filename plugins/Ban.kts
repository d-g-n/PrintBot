import com.github.decyg.command.CommandCore
import com.github.decyg.core.sendMessage
import com.github.decyg.tokenizer.TokenEnum

CommandCore.command {
    commandAliases = listOf("ban")
    prettyName = "Ban"
    description = "Bans people"
    argumentParams = listOf(
            TokenEnum.USER isOptional false isGreedy false,
            TokenEnum.TEXT isOptional true isGreedy true,
            TokenEnum.QUOTED_TEXT isOptional true isGreedy true
    )
    behaviour = { event, tokens ->
        event.sendMessage("go away")
    }
}

