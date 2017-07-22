
import com.github.decyg.command.CommandCore
import com.github.decyg.core.sendConfirmationEmbed
import com.github.decyg.permissions.RoleLevel

CommandCore.command {
    commandAliases = listOf("help")
    prettyName = "Help"
    description = "Displays the list of commands"
    requiredPermission = RoleLevel.EVERYONE
    argumentParams = emptyList()
    behaviour = { event, tokens ->
        event.message.sendConfirmationEmbed("honk", "honk honk")
    }
}

