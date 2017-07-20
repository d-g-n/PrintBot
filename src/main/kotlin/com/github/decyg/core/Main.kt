package com.github.decyg.core

fun main(args: Array<String>) {

    if(args.size != 1){
        println("Please ensure that the first argument is the token of the bot, for example java -jar bot.jar TOKENHERE")
        return
    }

    DiscordCore.login(args[0])



}

