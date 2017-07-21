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

        val newInst = UserToken()
        val res = getRegexResult(inputString, userRegex)

        if(res.first == null)
            return Pair(null, res.second)

        var foundUser : IUser? = null

        RequestBuffer.request {
            foundUser = DiscordCore.client.fetchUser(res.first)
        }.get()

        newInst.underlyingString = res.first!!
        newInst.mentionedUser = foundUser

        return Pair(
                if(res.first != null && foundUser != null) newInst else null,
                res.second
        )
    }

    override fun toString(): String {
        return "User Mention"
    }

}

class TextToken : Token() {

    var isQuoted = false

    val quotedTextRegex = "^\"([^\"]+)\"".toRegex()
    val textRegex = "^(\\S+)".toRegex()

    override fun produceToken(inputString : String) : Pair<Token?, String> {
        // First match against quoted text, if match return that, otherwise match text

        val newInst = TextToken()
        val quotedRes = getRegexResult(inputString, quotedTextRegex)

        if(quotedRes.first != null){

            newInst.underlyingString = quotedRes.first!!
            newInst.isQuoted = true

            return Pair(
                    newInst,
                    quotedRes.second
            )
        }

        val textRes = getRegexResult(inputString, textRegex)

        newInst.underlyingString = textRes.first!!

        return Pair(
                if(textRes.first != null) newInst else null,
                textRes.second
        )
    }

    override fun toString(): String {
        return "Text"
    }

}