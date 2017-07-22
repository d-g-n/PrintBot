package com.github.decyg.command

import com.github.decyg.config.config
import com.github.decyg.core.DiscordCore
import com.github.decyg.core.sendErrorEmbed
import com.github.decyg.tokenizer.Token
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

object CommandHandler {

    @EventSubscriber
    fun onMessage(ev : MessageReceivedEvent){

        var messageString = ev.message.content
        var prefix = DiscordCore.configStore[config.defaultprefix]
        if(ev.guild != null){ // private channel, no prefix needed
            prefix = DiscordCore.guildConfigStore[ev.guild.id]!!.serverPrefix
        }

        if(!messageString.startsWith(prefix))
            return

        messageString = messageString.substring(prefix.length, messageString.length).trim()

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

        // found a valid command but check the perms before wasting cycles on processing args
        // unless the command is for everyone or the owner is the author

        if(ev.author != ev.guild.owner) {

            val roleList = ev.author.getRolesForGuild(ev.guild)
            val hasPermission = roleList.firstOrNull { it.permissions.contains(command.requiredPermission) } != null

            if (!hasPermission) {
                ev.channel.sendErrorEmbed(
                        header = "$command",
                        errorMessage = "This command requires the ${command.requiredPermission}."
                )
                return
            }

        }


        // next validate the list of tokens, how?
        // so in the argumentparams of each defined command they have a series of regex which can bit tokenize the
        // result

        val consumedTokens = mutableListOf<Token>()
        var consumingMessageString = messageString.substring(commandName.length, messageString.length).trim()

        command.argumentParams.forEach { token ->

            // If it's greedy i want to consume and add as many as i can, appending tokens to consumed tokens as i go
            // if it's optional and it's not a match that's fine, continue
            // if it's not optional and it's not a match panic/report to use
            // in the pair if the token is null it's not a match, the "rest of the string" is the second arg

            var curRes = token.produceToken(consumingMessageString)

            if(token.isGreedy){ // consume as many as possible, mind if the first is null then it's not a match

                while(curRes.first != null){

                    consumedTokens.add(curRes.first!!)
                    consumingMessageString = curRes.second

                    curRes = token.produceToken(consumingMessageString)
                }

            } else { // consume one

                if(curRes.first != null) { // success

                    consumedTokens.add(curRes.first!!)
                    consumingMessageString = curRes.second

                } else { // failure

                    if(!token.isOptional){ // if it's not optional this is an issue, alert the user
                        ev.channel.sendErrorEmbed(
                                header = "$command",
                                errorMessage = "Expected a \"$token\" token at position in message:\n" +
                                "`${
                                ev.message.content.substring(
                                        0,
                                        ev.message.content.length - consumingMessageString.length
                                ) + "^$consumingMessageString"
                                }`\n"
                        )
                        return
                    }

                }

            }

        }

        // hasn't consumed all the arguments, too many arguments warning?
        //if(userArguments.isNotEmpty())
         //   return

        // actually run the command
        command.behaviour(ev, consumedTokens)
    }

}