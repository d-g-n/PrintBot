package com.github.decyg.core

import com.github.decyg.tokenizer.Token
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.awt.Color
import java.util.function.Predicate
import kotlin.concurrent.thread

// Helper function to send a message using the requestbuffer
fun IChannel.sendBufferedMessage(message: String, secondsTimeout : Int = 30) {

    val sentMessage = RequestBuffer.request <IMessage> {
        this.sendMessage(message)
    }.get()

    if(secondsTimeout != 0) {
        thread {
            Thread.sleep(secondsTimeout * 1000L)

            if (!sentMessage.isDeleted) {
                RequestBuffer.request {
                    sentMessage.delete()
                }
            }
        }
    }
}

// Helper function to send a message using the requestbuffer
fun IChannel.sendInfoEmbed(header : String = "Info", bodyMessage : String = "", secondsTimeout : Int = 30) : IMessage {

    var builder = EmbedBuilder()
            .withColor(Color.BLUE)
            .withAuthorName(header)
            .withDescription(bodyMessage)

    if(secondsTimeout != 0)
        builder = builder.withFooterText("This message will time out and self delete in $secondsTimeout seconds.")

    val sentMessage = RequestBuffer.request <IMessage> { this.sendMessage(builder.build()) }.get()

    if(secondsTimeout != 0) {
        thread {
            Thread.sleep(secondsTimeout * 1000L)

            if (!sentMessage.isDeleted) {
                RequestBuffer.request {
                    sentMessage.delete()
                }
            }
        }
    }

    return sentMessage

}

fun IMessage.sendConfirmationEmbed(header : String = "Confirmation", bodyMessage : String = "", secondsTimeout : Int = 30) : Boolean {
    return this.channel.sendConfirmationEmbed(this.author, header, bodyMessage, secondsTimeout)
}

// Helper function to send a confirmation prompt to the user in the form of an embed and reactions Y, N
fun IChannel.sendConfirmationEmbed(confirmationUser : IUser, header : String = "Confirmation", bodyMessage : String = "", secondsTimeout : Int = 30) : Boolean {

    val builder = EmbedBuilder()
            .withColor(Color.YELLOW)
            .withAuthorName(header)
            .withDescription(bodyMessage)
            .withFooterText("In response to: ${confirmationUser.name}, this message will time out with denial in $secondsTimeout seconds")

    val sentMessage = RequestBuffer.request<IMessage> { this.sendMessage(builder.build()) }.get()

    RequestBuffer.request {
        sentMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")) // yes
    }.get()

    RequestBuffer.request {
        sentMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")) // no
    }.get()

    val returnEvent = DiscordCore.client.dispatcher.waitFor(Predicate {
        (it is ReactionAddEvent) && it.message == sentMessage && it.user == confirmationUser &&
                (it.reaction.emoji.name == "\uD83C\uDDFE" || it.reaction.emoji.name == "\uD83C\uDDF3")
    }, secondsTimeout * 1000L)

    RequestBuffer.request {
        sentMessage.delete()
    }


    return (returnEvent != null && (returnEvent as ReactionAddEvent).reaction.emoji.name == "\uD83C\uDDFE")
}

// Helper function to send an error message embed
fun IChannel.sendErrorEmbed(header : String = "Error", errorMessage : String = "", secondsTimeout : Int = 30) {

    val builder = EmbedBuilder()
            .withColor(Color.RED)
            .withAuthorName(header)
            .withDescription(errorMessage)

    val sentMessage = RequestBuffer.request <IMessage> {
        this.sendMessage(builder.build())
    }.get()

    if(secondsTimeout != 0) {
        thread {
            Thread.sleep(secondsTimeout * 1000L)

            if (!sentMessage.isDeleted) {
                RequestBuffer.request {
                    sentMessage.delete()
                }
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

    sentMessage.addReaction(ReactionEmoji.of("\u2716"))

    val query = (DiscordCore.client.dispatcher.waitFor(Predicate {
        (it is MessageReceivedEvent) && it.author == userToWaitFor && it.channel == this ||
                (it is ReactionAddEvent) && it.message == sentMessage && it.user == userToWaitFor && it.reaction.emoji.name == "\u2716"
    }, secondsTimeout * 1000L))

    RequestBuffer.request {
        sentMessage.delete()
    }

    if(query != null && query is MessageReceivedEvent){
        return query.message.content.trim()
    }

    return null
}

fun IMessage.indicateSuccess(){
    RequestBuffer.request {
        this.addReaction(ReactionEmoji.of("\uD83D\uDC4C"))
    }
}

fun IGuild.getServerConfig() = DiscordCore.guildConfigStore[this.stringID]!!.configMap
fun IGuild.getPluginConfig() = DiscordCore.guildConfigStore[this.stringID]!!.pluginSettings

// Helper function to collapse a list of tokens into a space seperated string
fun List<Token>.asMessageString(concatAfterIndex : Int = 0) : String {

    var tempStr = ""

    this.forEachIndexed { index, token ->
        if(index > concatAfterIndex)
            tempStr = tempStr + token.underlyingString + " "
    }

    return tempStr.trim()

}