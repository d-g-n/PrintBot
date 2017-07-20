package com.github.decyg.core

import java.io.File
import javax.script.ScriptEngineManager

fun main(args: Array<String>) {

    if(args.size != 1){
        println("Please ensure that the first argument is the token of the bot, for example java -jar bot.jar TOKENHERE")
        return
    }

    DiscordCore.login(args[0])

    val engine = ScriptEngineManager().getEngineByExtension("kts")!!

    val res = engine.eval(File("plugins/test.kts").reader())

    println(res)

}

