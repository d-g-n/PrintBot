
import com.github.decyg.command.CommandCore
import com.github.decyg.core.sendBufferedMessage
import com.github.decyg.core.sendConfirmationMessage

CommandCore.command {
    commandAliases = listOf("help")
    prettyName = "Help"
    description = "Displays the list of commands"
    argumentParams = emptyList()
    behaviour = { event, tokens ->
        event.sendConfirmationMessage("honk", "honk honk")
    }
}

