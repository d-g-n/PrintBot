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
import kotlin.concurrent.thread

// Helper function to send a message using the requestbuffer
fun IChannel.sendBufferedMessage(message: String, secondsTimeout : Int = 5) {

    val sentMessage = RequestBuffer.request <IMessage> {
        this.sendMessage(message)
    }.get()

    thread {
        Thread.sleep(secondsTimeout * 1000L)

        if(!sentMessage.isDeleted) {
            RequestBuffer.request {
                sentMessage.delete()
            }
        }
    }
}

// Helper function to send a message using the requestbuffer
fun IChannel.sendInfoEmbed(header : String = "Info", bodyMessage : String = "", secondsTimeout : Int = 5) : IMessage {
    val builder = EmbedBuilder()
            .withColor(Color.BLUE)
            .withAuthorName(header)
            .withDescription(bodyMessage)

    val sentMessage = RequestBuffer.request <IMessage> { this.sendMessage(builder.build()) }.get()

    thread {
        Thread.sleep(secondsTimeout * 1000L)

        if(!sentMessage.isDeleted) {
            RequestBuffer.request {
                sentMessage.delete()
            }
        }
    }

    return sentMessage

}

fun IMessage.sendConfirmationEmbed(header : String = "Confirmation", bodyMessage : String = "", secondsTimeout : Int = 5) : Boolean {
    return this.channel.sendConfirmationEmbed(this.author, header, bodyMessage, secondsTimeout)
}

// Helper function to send a confirmation prompt to the user in the form of an embed and reactions Y, N
fun IChannel.sendConfirmationEmbed(confirmationUser : IUser, header : String = "Confirmation", bodyMessage : String = "", secondsTimeout : Int = 5) : Boolean {

    val builder = EmbedBuilder()
            .withColor(Color.YELLOW)
            .withAuthorName(header)
            .withDescription(bodyMessage)
            .withFooterText("In response to: ${confirmationUser.name}, this message will time out with denial in $secondsTimeout seconds")

    val sentMessage = RequestBuffer.request<IMessage> { this.sendMessage(builder.build()) }.get()

    RequestBuffer.request {
        sentMessage.addReaction("\uD83C\uDDFE") // yes
    }.get()

    RequestBuffer.request {
        sentMessage.addReaction("\uD83C\uDDF3") // no
    }.get()

    val returnEvent = DiscordCore.client.dispatcher.waitFor(Predicate {
        (it is ReactionAddEvent) && it.message == sentMessage && it.user == confirmationUser &&
                (it.reaction.unicodeEmoji.unicode == "\uD83C\uDDFE" || it.reaction.unicodeEmoji.unicode == "\uD83C\uDDF3")
    }, secondsTimeout * 1000L)

    RequestBuffer.request {
        sentMessage.delete()
    }

    return (returnEvent != null && (returnEvent as ReactionAddEvent).reaction.unicodeEmoji.unicode == "\uD83C\uDDFE")
}

// Helper function to send an error message embed
fun IChannel.sendErrorEmbed(header : String = "Error", errorMessage : String = "", secondsTimeout : Int = 10) {

    val builder = EmbedBuilder()
            .withColor(Color.RED)
            .withAuthorName(header)
            .withDescription(errorMessage)

    val sentMessage = RequestBuffer.request <IMessage> {
        this.sendMessage(builder.build())
    }.get()

    thread {
        Thread.sleep(secondsTimeout * 1000L)

        if(!sentMessage.isDeleted) {
            RequestBuffer.request {
                sentMessage.delete()
            }
        }
    }

}

fun IChannel.getUserResponse(
        userToWaitFor : IUser,
        secondsTimeout : Int = 30,
        header : String = "Waiting on input from ${userToWaitFor.name}...",
        bodyMessage : String = "Please enter a response in $secondsTimeout seconds or press X to cancel"
) : String? {

    val sentMessage = this.sendInfoEmbed(header, bodyMessage, secondsTimeout)

    sentMessage.addReaction("\u2716")

    val query = (DiscordCore.client.dispatcher.waitFor(Predicate {
        (it is MessageReceivedEvent) && it.author == userToWaitFor && it.channel == this ||
                (it is ReactionAddEvent) && it.message == sentMessage && it.user == userToWaitFor && it.reaction.unicodeEmoji.unicode == "\u2716"
    }, secondsTimeout * 1000L))

    RequestBuffer.request {
        sentMessage.delete()
    }

    if(query != null && query is MessageReceivedEvent){
        return query.message.content.trim()
    }

    return null
}

// Helper function to collapse a list of tokens into a space seperated string
fun MutableList<Token>.asMessageString() : String {

    var tempStr = ""

    this.forEach { tempStr = tempStr + it.underlyingString + " " }

    return tempStr.trim()

}