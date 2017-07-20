package com.github.decyg.tokenizer

import sx.blah.discord.handle.obj.IUser

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

    // Helper function to collapse a list of tokens into a space seperated string
    fun MutableList<Token>.asMessageString() : String {

        var tempStr = ""

        this.forEach { tempStr = tempStr + it.underlyingString + " " }

        return tempStr.trim()

    }

}

interface Token{
    val underlyingString : String
}

data class TimeToken(
        override var underlyingString: String,
        val timeMillis : Long
) : Token

data class UserToken(
        override var underlyingString: String,
        val mentionedUser : IUser
) : Token

data class TextToken(
        override var underlyingString: String,
        var isQuoted : Boolean
) : Token