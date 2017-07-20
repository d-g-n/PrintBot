package com.github.decyg.core

import com.github.decyg.command.CommandHandler
import com.github.decyg.command.CommandStore
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import java.io.File

/**
 * Created by declan on 15/07/2017.
 */
object DiscordCore {

    lateinit var client : IDiscordClient
    val logger = LoggerFactory.getLogger("com.github.decyg.PrintBot")

    val configStore = EnvironmentVariables() overriding
            ConfigurationProperties.fromFile(File("config/config.properties"))

    fun login(token : String){

        client = ClientBuilder()
                .withToken(token)
                .build()

        client.dispatcher.registerListener(this)
        client.dispatcher.registerListener(CommandHandler)

        client.login()

    }

    @EventSubscriber
    fun onReady(ev : ReadyEvent){

        CommandStore.registerAllPlugins()

    }

}