package utility

import com.github.decyg.command.CommandCore
import com.github.decyg.core.AuditLog
import com.github.decyg.core.sendInfoEmbed
import com.github.decyg.tokenizer.TextToken
import com.github.decyg.tokenizer.UserToken
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

CommandCore.command {
    commandAliases = listOf("status")
    prettyName = "Status"
    description = "Returns the information for a user"
    requiredPermission = Permissions.ADMINISTRATOR
    argumentParams = listOf(
            UserToken("a user to check info for") isOptional false isGreedy false
    )
    behaviour = { event, tokens ->

        val user = tokens[0] as UserToken
        val joinDate = event.guild.getJoinTimeForUser(user.mentionedUser!!)

        val embedB = EmbedBuilder()
        embedB.appendField("Username", user.mentionedUser?.name ?: "", false)
        embedB.appendField("Join Date", joinDate.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME) ?: "", false)
        embedB.appendField("Join Days", ChronoUnit.DAYS.between(joinDate.atZone(ZoneId.systemDefault()), ZonedDateTime.now()).toString() + " days", false)
        embedB.appendField("Avatar URL", user.mentionedUser?.avatarURL ?: "", false)

        RequestBuffer.request { event.channel.sendMessage(embedB.build()) }

    }
}