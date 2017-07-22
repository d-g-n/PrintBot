package com.github.decyg.core

import com.github.decyg.tokenizer.Token
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.awt.Color
import java.util.function.Predicate

// Helper function to send a message using the requestbuffer
fun IChannel.sendBufferedMessage(message: String) {
    RequestBuffer.request {
        this.sendMessage(message)
    }.get()
}

// Helper function to send a message using the requestbuffer
fun IChannel.sendInfoEmbed(header : String = "Info", bodyMessage : String = "") {
    val builder = EmbedBuilder()
            .withColor(Color.BLUE)
            .withAuthorName(header)
            .withDescription(bodyMessage)

    RequestBuffer.request { this.sendMessage(builder.build()) }.get()

}

// Helper function to send a confirmation prompt to the user in the form of an embed and reactions Y, N
fun IMessage.sendConfirmationEmbed(header : String = "Confirmation", bodyMessage : String = "") : Boolean {

    val builder = EmbedBuilder()
            .withColor(Color.YELLOW)
            .withAuthorName(header)
            .withDescription(bodyMessage)
            .withFooterText("In response to: ${this.author.name}, this message will time out with denial in five seconds")

    val sentMessage = RequestBuffer.request<IMessage> { this.channel.sendMessage(builder.build()) }.get()

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
fun IChannel.sendErrorEmbed(header : String = "Error", errorMessage : String = "") {

    val builder = EmbedBuilder()
            .withColor(Color.RED)
            .withAuthorName(header)
            .withDescription(errorMessage)

    RequestBuffer.request {
        this.sendMessage(builder.build())
    }

}

fun IChannel.getUserResponse(userToWaitFor : IUser) : String {

    this.sendInfoEmbed("Waiting on input...", "Please enter a response within 30 seconds")

    return ((DiscordCore.client.dispatcher.waitFor(Predicate {
        (it is MessageReceivedEvent) && it.author == userToWaitFor
    }, 30000)) as MessageReceivedEvent).message.content
}

// Helper function to collapse a list of tokens into a space seperated string
fun MutableList<Token>.asMessageString() : String {

    var tempStr = ""

    this.forEach { tempStr = tempStr + it.underlyingString + " " }

    return tempStr.trim()

}