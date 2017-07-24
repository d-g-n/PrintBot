package com.github.decyg.command

import com.github.decyg.tokenizer.Token
import sx.blah.discord.api.events.Event
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions

/**
 * Created by declan on 15/07/2017.
 */
object CommandCore {

    // Type safe builder definitions

    class Command {
        lateinit var commandAliases : List<String>
        lateinit var prettyName : String
        lateinit var description : String
        lateinit var requiredPermission : Permissions
        lateinit var argumentParams : List<Token>
        lateinit var behaviour : (MessageReceivedEvent, List<Token>) -> Unit
        var passiveListener : ((Event) -> Unit)? = null


        override fun toString(): String {
            var outputString = ""
            commandAliases.forEach {
                outputString = "$outputString$it|"
            }

            outputString = outputString.substring(0, outputString.length - 1) + " "

            argumentParams.forEach {

                if (it.isOptional) {
                    outputString += "[${it.description}]"
                } else {
                    outputString += "<${it.description}>"
                }

                if(it.isGreedy)
                    outputString += "+"

                outputString += " "
            }

            return outputString.trim()
        }
    }

    fun command(init : Command.() -> Unit) : Command {
        val command = Command()
        command.init()
        return command
    }

}