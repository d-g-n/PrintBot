package com.github.decyg.core

import sx.blah.discord.handle.obj.IGuild

object AuditLog {

    fun log(guild : IGuild, details : String){

        DiscordCore.logger.info("[${guild.name}] $details")

        val logChannelID = DiscordCore.guildConfigStore[guild.stringID]?.configMap!!["auditLogChannelID"] as String ?: ""

        if(logChannelID == "")
            return

        guild.getChannelByID(logChannelID)?.sendInfoEmbed(
                header = "Audit Log",
                bodyMessage = details,
                secondsTimeout = 0
        )

    }

}