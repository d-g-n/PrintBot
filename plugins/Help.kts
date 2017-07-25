
import com.github.decyg.command.CommandCore
import com.github.decyg.command.CommandStore
import com.github.decyg.core.sendInfoEmbed
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("help")
    prettyName = "Help"
    description = "Displays the list of commands"
    requiredPermission = Permissions.SEND_MESSAGES
    argumentParams = emptyList()
    behaviour = { event, _ ->

        println(CommandStore.commandStore.entries)

        event.channel.sendInfoEmbed(
                header = "Command list",
                bodyMessage = CommandStore.commandStore.entries.fold("", { init, curObj ->
                    init +
                            "```\n" +
                            "Name: ${curObj.value.prettyName}\n" +
                            "Permission required: ${curObj.value.requiredPermission}\n" +
                            "Description: ${curObj.value.description}\n" +
                            "Usage: ${curObj.value.toString()}\n" +
                            "```\n"
                })
        )

    }
}

