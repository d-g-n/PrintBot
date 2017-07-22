package com.github.decyg.permissions

import com.github.decyg.core.DiscordCore
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IRole

// Hierarchy of roles, a higher role can use any roles of the below roles
// Server owner is an internal role and supersedes all roles
enum class RoleLevel {
    EVERYONE,
    TRUSTED,
    MODERATOR,
    ADMINISTRATOR,
    OWNER;

    fun getRolesForGuild(guild : IGuild) : List<IRole> {
        val guildConfig = DiscordCore.guildConfigStore[guild.stringID]!!

        return when(this){
            EVERYONE -> listOf(guild.everyoneRole)
            TRUSTED -> guildConfig.trustedRoleIDs.map { guild.getRoleByID(it) } + EVERYONE.getRolesForGuild(guild)
            MODERATOR -> guildConfig.moderatorRoleIDs.map { guild.getRoleByID(it) } + TRUSTED.getRolesForGuild(guild)
            ADMINISTRATOR -> guildConfig.administratorRoleIDs.map { guild.getRoleByID(it) } + MODERATOR.getRolesForGuild(guild)
            else -> emptyList<IRole>()
        }
    }
}