
import com.github.decyg.command.CommandCore
import com.github.decyg.core.sendConfirmationEmbed
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("help")
    prettyName = "Help"
    description = "Displays the list of commands"
    requiredPermission = Permissions.SEND_MESSAGES
    argumentParams = emptyList()
    behaviour = { event, _ ->
        event.message.sendConfirmationEmbed("honk", "honk honk")
    }
}

