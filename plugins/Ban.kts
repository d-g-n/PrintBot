

import com.github.decyg.command.CommandCore
import com.github.decyg.core.asMessageString
import com.github.decyg.core.sendConfirmationEmbed
import com.github.decyg.core.sendErrorEmbed
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.UserToken
import org.ocpsoft.prettytime.nlp.PrettyTimeParser
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("ban")
    prettyName = "Ban"
    description = "Bans people"
    requiredPermission = Permissions.BAN
    argumentParams = listOf(
            UserToken("a user to ban") isOptional false isGreedy false,
            TextToken("how long for and reason") isOptional false isGreedy true
    )
    behaviour = { event, tokens ->

        val user = tokens[0] as UserToken
        val lengthAndReason = tokens.asMessageString(0)

        val banTimes = PrettyTimeParser().parse(lengthAndReason)

        if(banTimes.size == 2){

            val banBool = event.channel.sendConfirmationEmbed(
                    event.author,
                    bodyMessage = "Are you really sure you want to ban ${user.mentionedUser?.name ?: ""} (${user.mentionedUser!!.stringID})" +
                            " until ${banTimes[1]} for the reason `$lengthAndReason`"
            )

            if(banBool)
                event.guild.banUser(user.mentionedUser, lengthAndReason)

        } else {
            event.channel.sendErrorEmbed(errorMessage = "No length found in your reason.\n" +
                    "A correct usage is `ban user for ten days due to something`")
        }
    }
}

