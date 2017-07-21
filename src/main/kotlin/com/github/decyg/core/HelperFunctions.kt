package com.github.decyg.core

import com.github.decyg.tokenizer.Token
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.awt.Color

// Helper function to send a message using the requestbuffer
fun MessageReceivedEvent.sendBufferedMessage(message: String) {
    RequestBuffer.request {
        this.message.reply(message)
    }.get()
}

fun MessageReceivedEvent.sendErrorMessage(errorMessage : String) {

    val builder = EmbedBuilder()
            .withColor(Color.RED)
            .withAuthorName("Error")
            .withDescription(errorMessage)
            .withFooterText("In response to: ${this.author.name}")

    RequestBuffer.request {
        this.message.channel.sendMessage(builder.build())
    }

}

// Helper function to collapse a list of tokens into a space seperated string
fun MutableList<Token>.asMessageString() : String {

    var tempStr = ""

    this.forEach { tempStr = tempStr + it.underlyingString + " " }

    return tempStr.trim()

}
