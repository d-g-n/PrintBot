package com.github.decyg.core

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.decyg.command.CommandHandler
import com.github.decyg.command.CommandStore
import com.github.decyg.permissions.PermissionPOKO
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent
import java.io.File

/**
 * Created by declan on 15/07/2017.
 */
object DiscordCore {

    lateinit var client : IDiscordClient

    val mapper = jacksonObjectMapper()
    val guildConfigStore = mutableMapOf<String, PermissionPOKO>()
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

    @EventSubscriber
    fun onGuildCreate(ev : GuildCreateEvent) {

        // check if the guild has a respective json, if not create one

        val guildConfigFolder = File(configStore[config.guildconfigfolder])

        if(!guildConfigFolder.exists())
            guildConfigFolder.mkdirs()

        val guildConfigFile = File(guildConfigFolder, "${ev.guild.longID}.json")

        if(guildConfigFile.exists()){
            // add it to global store
            val poko = mapper.readValue<PermissionPOKO>(guildConfigFile)

            guildConfigStore.put(ev.guild.longID.toString(), poko)

        } else {
            mapper.writeValue(guildConfigFile, PermissionPOKO())
        }

    }

}