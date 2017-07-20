package com.github.decyg.tokenizer

import com.github.decyg.core.DiscordCore
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer

enum class TokenEnum(val regex : Regex, private val tokenProducer : TokenEnum.(String) -> Pair<Token?, String>) {

    // For all these they take in the argument string sans the
    // returns a pair that is the found token, or null if not found, and the rest of the string

    USER(
            "^<@!?(\\d+)>".toRegex(),
            { inputString ->

                val res = getRegexResult(inputString, USER.regex)

                if(res.first == null)
                    Pair(null, res.second)

                var foundUser : IUser? = null

                RequestBuffer.request {
                    foundUser = DiscordCore.client.fetchUser(res.first)
                }.get()

                Pair(
                        if(res.first != null && foundUser != null) UserToken(res.first!!, foundUser!!) else null,
                        res.second
                )

            }
    ),
    QUOTED_TEXT(
            "^\"([^\"]+)\"".toRegex(),
            { inputString ->

                val res = getRegexResult(inputString, QUOTED_TEXT.regex)

                Pair(
                        if(res.first != null) TextToken(res.first!!, true) else null,
                        res.second
                )

            }
    ),
    TEXT(
            "^(\\S+)".toRegex(),
            { inputString ->

                val res = getRegexResult(inputString, TEXT.regex)

                Pair(
                        if(res.first != null) TextToken(res.first!!, false) else null,
                        res.second
                )

            }
    );

    // Defines if this instance of the enum is "optional" as in if the command parser looks to match it and can't find it
    // skip it
    var isOptional = false
    // Defines if the instance of the enum is "greedy" which means should it look and consume as many tokens in order of
    // said type, useful for consuming a pile of text after required params like a ban reason or something
    var isGreedy = false

    // Util function to quick match the regex against the input, if it's null then
    // No match has found and the token should be null to move on, it'll be a string otherwise
    // in pair (found res, rest of string)
    private fun getRegexResult(inputString : String, regex : Regex) : Pair<String?, String> {

        val matchRes = regex.find(inputString)

        val foundRes = matchRes?.groups?.get(1)?.value
        val restOfString = inputString.subSequence(matchRes?.value?.length ?: 0, inputString.length).toString().trim()

        return Pair(foundRes, restOfString)
    }

    // public safe function for calling the processing functions
    fun produceToken(input : String) : Pair<Token?, String> = this.tokenProducer(input)

    infix fun isOptional(isOpt : Boolean) : TokenEnum{
        this.isOptional = isOpt
        return this
    }

    infix fun isGreedy(isGreed : Boolean) : TokenEnum{
        this.isGreedy = isGreed
        return this
    }

}