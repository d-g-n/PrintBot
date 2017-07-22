package com.github.decyg.core

import com.github.decyg.tokenizer.Token
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.awt.Color
import java.util.function.Predicate

// Helper function to send a message using the requestbuffer
fun MessageReceivedEvent.sendBufferedMessage(message: String) {
    RequestBuffer.request {
        this.message.reply(message)
    }.get()
}

// Helper function to send a confirmation prompt to the user in the form of an embed and reactions Y, N
fun MessageReceivedEvent.sendConfirmationMessage(header : String = "Confirmation", bodyMessage : String = "") : Boolean {

    val builder = EmbedBuilder()
            .withColor(Color.BLUE)
            .withAuthorName(header)
            .withDescription(bodyMessage)
            .withFooterText("In response to: ${this.author.name}, this message will time out with denial in five seconds")

    val sentMessage = RequestBuffer.request<IMessage> { this.message.channel.sendMessage(builder.build()) }.get()

    RequestBuffer.request {
        sentMessage.addReaction("\uD83C\uDDFE")
    }.get()

    RequestBuffer.request {
        sentMessage.addReaction("\uD83C\uDDF3")
    }.get()

    val returnEvent = DiscordCore.client.dispatcher.waitFor(Predicate {
        (it is ReactionAddEvent) && it.message == sentMessage && it.user == this.author
    }, 5000)

    RequestBuffer.request {
        sentMessage.delete()
    }

    return (returnEvent != null)
}

// Helper function to send an error message embed
fun MessageReceivedEvent.sendErrorMessage(header : String = "Error", errorMessage : String = "") {

    val builder = EmbedBuilder()
            .withColor(Color.RED)
            .withAuthorName(header)
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