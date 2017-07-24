package com.github.decyg.config

import com.github.decyg.core.DiscordCore

data class ConfigPOKO(
        var configMap : MutableMap<String, String> = mutableMapOf(
                "serverPrefix" to DiscordCore.configStore[config.defaultprefix],
                "auditLogChannelID" to ""
        ),
        var pluginSettings : MutableMap<String, String> = mutableMapOf()
)