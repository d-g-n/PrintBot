package com.github.decyg.tokenizer

import com.github.decyg.core.DiscordCore
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer


sealed class Token {

    var underlyingString : String = ""
    var isOptional : Boolean = false
    var isGreedy : Boolean = false

    abstract fun produceToken(inputString : String) : Pair<Token?, String>

    internal fun getRegexResult(inputString : String, regex : Regex) : Pair<String?, String> {

        val matchRes = regex.find(inputString)

        val foundRes = matchRes?.groups?.get(1)?.value
        val restOfString = inputString.subSequence(matchRes?.value?.length ?: 0, inputString.length).toString().trim()

        return Pair(foundRes, restOfString)
    }

    infix fun isOptional(isOpt : Boolean) : Token{
        this.isOptional = isOpt
        return this
    }

    infix fun isGreedy(isGreed : Boolean) : Token{
        this.isGreedy = isGreed
        return this
    }

}

class TimeToken : Token() {

    var timeMillis : Long = 0

    override fun produceToken(inputString : String): Pair<Token?, String> {
        return Pair(null, "")
    }

}

class UserToken : Token() {

    var mentionedUser : IUser? = null

    val userRegex = "^<@!?(\\d+)>".toRegex()

    override fun produceToken(inputString : String): Pair<Token?, String> {
        val res = getRegexResult(inputString, userRegex)

        if(res.first == null)
            Pair(null, res.second)

        var foundUser : IUser? = null

        RequestBuffer.request {
            foundUser = DiscordCore.client.fetchUser(res.first)
        }.get()

        underlyingString = res.first!!
        mentionedUser = foundUser

        return Pair(
                if(res.first != null && foundUser != null) this else null,
                res.second
        )
    }

}

class TextToken : Token() {

    var isQuoted = false

    val quotedTextRegex = "^\"([^\"]+)\"".toRegex()
    val textRegex = "^(\\S+)".toRegex()

    override fun produceToken(inputString : String) : Pair<Token?, String> {
        // First match against quoted text, if match return that, otherwise match text

        val quotedRes = getRegexResult(inputString, quotedTextRegex)

        if(quotedRes.first != null){

            underlyingString = quotedRes.first!!
            isQuoted = true

            return Pair(
                    this,
                    quotedRes.second
            )
        }

        val textRes = getRegexResult(inputString, textRegex)

        return Pair(
                if(textRes.first != null) this else null,
                textRes.second
        )
    }

}