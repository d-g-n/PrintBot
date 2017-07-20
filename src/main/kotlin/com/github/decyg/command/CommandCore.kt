package com.github.decyg.command

import com.github.decyg.tokenizer.Token
import com.github.decyg.tokenizer.TokenEnum
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by declan on 15/07/2017.
 */
object CommandCore {

    // Type safe builder definitions

    interface CommandInterface {
        val command : Command
    }

    class Command {
        lateinit var commandAliases : List<String>
        lateinit var prettyName : String
        lateinit var description : String
        lateinit var argumentParams : List<TokenEnum>
        lateinit var behaviour : (MessageReceivedEvent, List<Token>) -> Unit
    }

    fun command(init : Command.() -> Unit) : Command {
        val command = Command()
        command.init()
        return command
    }

}