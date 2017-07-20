package com.github.decyg.core

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.RequestBuffer

fun MessageReceivedEvent.sendMessage(s: String) {
    RequestBuffer.request {
        this.message.reply(s)
    }
}
