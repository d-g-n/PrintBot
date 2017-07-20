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

        if(!CommandStore.commandStore.containsKey(commandName))
            return

        val command = CommandStore.commandStore[commandName]!!

        // next validate the list of tokens, how?
        // basically get the list of modified enums in the command.argumentparams into a local list, go through
        // it one by one and for each accept "just" that argument or one or more
        // it's just a validation pass so it should just be a simple "i expect one or more etc" and it'll break out
        // and return with error if it gets something unexpected

        var userArguments = tokenizeRes

        command.argumentParams.forEach { tokEnum ->

            // if it's greedy keep consuming tokens from userarguments until userarguments is empty or it bumps into a token that doesn't match
            if(tokEnum.isGreedy){

                tokEnum.matchesToken(tokenizeRes[0])

            } else {
                // if it's

            }

        }

        // hasn't consumed all the arguments,
        if(userArguments.isNotEmpty())
            return


        println(commandName)
        println(tokenizeRes)
    }

}