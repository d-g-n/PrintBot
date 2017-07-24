

import com.github.decyg.command.CommandCore
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

    }
}

