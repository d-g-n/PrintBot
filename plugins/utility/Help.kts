
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

                    if(event.getMessage().toString().split(" ").length > 1){
                        Command command = CommandStore.commandStore.entries.get(event.getMessage().toString().split(" ")[1]);
                        event.getChannel().sendMessage(command.prettyName + ": " + command.description + " !" + command.commandAliases[0] + " " + command.argumentParams + " \nNeeded Permission(s): " + command.requiredPermission);
	    			}
	    			else{
	    				event.getChannel().sendMessage("Commands:\n```"+ Arrays.toString(CommandStore.commandStore.entries.toTypedArray()).replace("[", "").replace("]", "")+"```\nUsage: !help <command>");
	    			}
    }
}

