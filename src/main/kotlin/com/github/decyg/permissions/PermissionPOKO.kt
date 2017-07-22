package com.github.decyg.permissions

data class PermissionPOKO (
        val serverPrefix : String = "!!",
        val userRoleIDs : List<String> = emptyList(),
        val moderatorRoleIDs : List<String> = emptyList(),
        val administratorRoleIDs : List<String> = emptyList()
)