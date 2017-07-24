package com.github.decyg.command

import com.github.decyg.config.config
import com.github.decyg.core.DiscordCore
import java.io.File
import javax.script.ScriptEngineManager

object CommandStore {

    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    val commandStore : MutableMap<String, CommandCore.Command> = mutableMapOf()

    fun registerAllPlugins() {

        // clean out the existing commandstore

        commandStore.clear()
        DiscordCore.logger.info("Successfully cleared all registered commands")

        val pluginFolder = File(DiscordCore.configStore[config.pluginfolder])
        
        pluginFolder.listFiles().forEach {

            try {

                val curRes = engine.eval(it.reader()) as CommandCore.Command

                curRes.commandAliases.forEach {

                    commandStore.put(it, curRes)

                    if(curRes.passiveListener != null)
                        DiscordCore.client.dispatcher.registerListener(curRes.passiveListener)

                    DiscordCore.logger.info("Successfully registered command: ${curRes.prettyName} with alias $it")

                }

            } catch (e : Exception) {
                DiscordCore.logger.error("Command: ${it.nameWithoutExtension} could not be registered with reason: ", e)
            }

        }

    }

}