package com.github.decyg.command

import com.github.decyg.core.DiscordCore
import com.github.decyg.core.config
import com.github.decyg.tokenizer.Tokenizer
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

object CommandHandler {

    @EventSubscriber
    fun onMessage(ev : MessageReceivedEvent){

        var messageString = ev.message.content
        val prefix = DiscordCore.configStore[config.prefix]

        if(!messageString.startsWith(prefix))
            return

        messageString = messageString.subSequence(prefix.length, messageString.length).toString().trim()

        if(messageString.isEmpty())
            return

        val tokenizeRes = Tokenizer.tokenize(messageString)

        if(tokenizeRes.isEmpty())
            return

        val commandName = tokenizeRes[0].underlyingString
        tokenizeRes.removeAt(0)

        // at this point i have both the command name and list of available tokens
        // i need to first find any applicable command in the command map and then match the args if i can
        // if a command isn't found, do nothing, if a command is found but the wrong args are entered say what went
        // wrong


        println(commandName)
        println(tokenizeRes)
    }

}