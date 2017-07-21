package com.github.decyg.command

import com.github.decyg.core.DiscordCore
import com.github.decyg.core.config
import com.github.decyg.tokenizer.Token
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by declan on 15/07/2017.
 */
object CommandCore {

    // Type safe builder definitions

    class Command {
        lateinit var commandAliases : List<String>
        lateinit var prettyName : String
        lateinit var description : String
        lateinit var argumentParams : List<Token>
        lateinit var behaviour : (MessageReceivedEvent, List<Token>) -> Unit

        override fun toString(): String {
            var outputString = DiscordCore.configStore[config.prefix]

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