package com.github.decyg.core

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.decyg.command.CommandHandler
import com.github.decyg.command.CommandStore
import com.github.decyg.config.ConfigPOKO
import com.github.decyg.config.config
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent
import sx.blah.discord.handle.obj.IGuild
import java.io.File

/**
 * Created by declan on 15/07/2017.
 */
object DiscordCore {

    lateinit var client : IDiscordClient

    val mapper = jacksonObjectMapper()
    val logger = LoggerFactory.getLogger("com.github.decyg.PrintBot")!!

    val guildConfigStore = mutableMapOf<String, ConfigPOKO>()
    val globalConfigStore = EnvironmentVariables() overriding
            ConfigurationProperties.fromFile(File("config/config.properties"))

    fun login(token : String){

        client = ClientBuilder()
                .withToken(token)
                .build()

        client.dispatcher.registerListener(this)
        client.dispatcher.registerListener(CommandHandler)

        CommandStore.registerAllPlugins()

        client.login()

    }

    @EventSubscriber
    fun onGuildCreate(ev : GuildCreateEvent) {

        // check if the guild has a respective json, if not create one

        val guildConfigFolder = File(globalConfigStore[config.guildconfigfolder])

        if(!guildConfigFolder.exists())
            guildConfigFolder.mkdirs()

        val guildConfigFile = File(guildConfigFolder, "${ev.guild.longID}.json")
        var poko = ConfigPOKO()

        if(guildConfigFile.exists()){
            // add it to global store
            poko = mapper.readValue<ConfigPOKO>(guildConfigFile)

        } else {

            mapper.writeValue(guildConfigFile, poko)

        }

        guildConfigStore.put(ev.guild.longID.toString(), poko)


        logger.info("Loaded config for guild id ${ev.guild.longID} successfully!")

    }

    fun getConfigForGuild(guild : IGuild) : ConfigPOKO{
        return guildConfigStore[guild.stringID]!!
    }

    fun updateGuildConfigStore(chosenGuild: String){

        synchronized(this, {
            val guildConfigFolder = File(globalConfigStore[config.guildconfigfolder])
            val guildConfigFile = File(guildConfigFolder, "$chosenGuild.json")

            mapper.writeValue(guildConfigFile, guildConfigStore[chosenGuild])

        })

    }

}