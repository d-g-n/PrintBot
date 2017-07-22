package com.github.decyg.permissions

import com.github.decyg.core.DiscordCore
import com.github.decyg.core.config

data class PermissionPOKO (
        val serverPrefix : String = DiscordCore.configStore[config.defaultprefix],
        val userRoleIDs : List<String> = emptyList(),
        val moderatorRoleIDs : List<String> = emptyList(),
        val administratorRoleIDs : List<String> = emptyList()
)