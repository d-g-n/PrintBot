package com.github.decyg.command

import com.github.decyg.core.DiscordCore
import com.github.decyg.core.config
import com.github.decyg.core.sendBufferedMessage
import com.github.decyg.tokenizer.Token
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

        // split the string by spaces and get the first, that's the command

        val tokenizeRes = messageString.split(" ")

        if(tokenizeRes.isEmpty())
            return

        val commandName = tokenizeRes[0]

        // at this point i have both the command name and list of available tokens
        // i need to first find any applicable command in the command map and then match the args if i can
        // if a command isn't found, do nothing, if a command is found but the wrong args are entered say what went
        // wrong

        if(!CommandStore.commandStore.containsKey(commandName))
            return

        val command = CommandStore.commandStore[commandName]!!

        // next validate the list of tokens, how?
        // so in the argumentparams of each defined command they have a series of regex which can bit tokenize the
        // result

        var consumedTokens = mutableListOf<Token>()
        var consumingMessageString = messageString.subSequence(commandName.length, messageString.length).toString().trim()

        command.argumentParams.forEach { token ->

            // If it's greedy i want to consume and add as many as i can, appending tokens to consumed tokens as i go
            // if it's optional and it's not a match that's fine, continue
            // if it's not optional and it's not a match panic/report to use
            // in the pair if the token is null it's not a match, the "rest of the string" is the second arg

            val curRes = token.produceToken(consumingMessageString)

            if(token.isGreedy){ // consume as many as possible, mind if the first is null then it's not a match

            } else { // consume one

                if(curRes.first != null) { // success

                    consumedTokens.add(curRes.first!!)
                    consumingMessageString = curRes.second

                } else { // failure

                    if(!token.isOptional){ // if it's not optional this is an issue, alert the user
                        ev.sendBufferedMessage("Expected a \"$token\" token at position \"^$consumingMessageString\"\n" +
                                "The method signature of $commandName is `$command`")
                    }

                }

            }

        }

        // hasn't consumed all the arguments, too many arguments warning?
        //if(userArguments.isNotEmpty())
         //   return


        println(commandName)
        println(consumingMessageString)
    }

}