package com.github.decyg.tokenizer

import com.github.decyg.core.DiscordCore
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer

/**
 * Created by declan on 16/07/2017.
 */
object Tokenizer {

    /*
    Budget tokenizer because pegs and regex are too slow or too fiddly to get working dynamically
    Basically, consume any amount of spaces and then descending rules for tokens, check forward to see if it's
    time etc and if it is process it
     */
    fun tokenize(input : String) : MutableList<Token> {

        var index = 0
        var exhaustiveTimeout = 0
        val resList = mutableListOf<Token>()
        var stringInProgress = input

        while (input.isNotEmpty() && exhaustiveTimeout <= TokenEnum.values().size){

            // on every iteration match against it, if succ then add it ad a token to the list of tokens and reset exha, if unsucc
            // move on and do again

            val curTokenProcessor = TokenEnum.values()[index]

            val tokenPair = curTokenProcessor.produceToken(stringInProgress)

            if(index == TokenEnum.values().size - 1){
                index = 0
            } else {
                index++
            }

            if(tokenPair.first == null){
                exhaustiveTimeout++
            } else {
                index = 0
                exhaustiveTimeout = 0
                resList.add(tokenPair.first!!)
                stringInProgress = tokenPair.second
            }

            println("honk")

        }

        return resList
    }



}
