package com.github.decyg.command

import com.github.decyg.config.config
import com.github.decyg.core.DiscordCore
import java.io.File

object CommandStore {

    init {
        class Dummy

        val JAR_PATH: String = Dummy::class.java.protectionDomain.codeSource.location.toURI().path //Locates jar file (this doesn't work too well when it isn't compiled to a jar)
        System.setProperty("kotlin.compiler.jar", JAR_PATH) //So kotlin doesn't like us packaging the compiler...Screw it
    }

    val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
    val commandStore : MutableMap<String, CommandCore.Command> = mutableMapOf()

    fun registerAllPlugins() {

        // clean out the existing commandstore

        commandStore.clear()
        DiscordCore.logger.info("Successfully cleared all registered commands")

        val pluginFolder = File(DiscordCore.globalConfigStore[config.pluginfolder])
        
        pluginFolder.listFiles().forEach {

            try {

                val curRes = engine.eval(it.reader()) as CommandCore.Command

                curRes.commandAliases.forEach {

                    commandStore.put(it, curRes)

                    if(curRes.passiveListener != null)
                        DiscordCore.client.dispatcher.registerListener(curRes.passiveListener)

                    DiscordCore.logger.info("Successfully registered command: ${curRes.prettyName} with alias $it")

                }

            } catch (e  :Throwable){
                // nothing to see here
            } catch (e : Exception) {
                DiscordCore.logger.error("Command: ${it.nameWithoutExtension} could not be registered with reason: ", e)
            }

        }

    }

}