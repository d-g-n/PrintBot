package com.github.decyg.permissions

import sx.blah.discord.handle.obj.IGuild

// Hierarchy of roles, a higher role can use any roles of the below roles
// Server owner is an internal role and supersedes all roles
enum class RoleLevel {
    EVERYONE,
    TRUSTED,
    MODERATOR,
    ADMINISTRATOR,
    OWNER;
}