package com.github.decyg.command

import com.github.decyg.core.DiscordCore
import com.github.decyg.core.config
import java.io.File
import javax.script.ScriptEngineManager

object CommandStore {

    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    val commandStore : MutableMap<String, CommandCore.Command> = mutableMapOf()

    fun registerAllPlugins() {

        val pluginFolder = File(DiscordCore.configStore[config.pluginfolder])
        
        pluginFolder.listFiles().forEach {

            try {

                val curRes = engine.eval(it.reader()) as CommandCore.Command

                curRes.commandAliases.forEach {

                    commandStore.put(it, curRes)

                }

            } catch (e : Exception) {
                println("bad thing happened, replace this with a logger later")
            }

        }

        println(commandStore)
    }

}