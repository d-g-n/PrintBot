package com.github.decyg.config

import com.github.decyg.core.DiscordCore

data class ConfigPOKO(
        var serverPrefix : String = DiscordCore.configStore[config.defaultprefix],
        var auditLogChannelID : String = "",
        var pluginSettings : Map<String, String> = emptyMap()
)