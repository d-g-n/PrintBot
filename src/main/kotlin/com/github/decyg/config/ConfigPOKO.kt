package com.github.decyg.config

import com.github.decyg.core.DiscordCore

data class ConfigPOKO(
        val serverPrefix : String = DiscordCore.configStore[config.defaultprefix],
        val auditLogChannelID : String = ""
)