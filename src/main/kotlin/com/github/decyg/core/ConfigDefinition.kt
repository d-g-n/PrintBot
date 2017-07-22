package com.github.decyg.core

import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType

object config : PropertyGroup() {
    val defaultprefix by stringType
    val pluginfolder by stringType
    val guildconfigfolder by stringType
}