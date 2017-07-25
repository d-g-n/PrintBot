
import com.github.decyg.command.CommandCore
import com.github.decyg.core.*
import com.github.decyg.tokenizer.TextToken
import sx.blah.discord.handle.obj.Permissions

CommandCore.command {
    commandAliases = listOf("settings")
    prettyName = "Guild Settings"
    description = "Displays the guild settings and allows a user to change them"
    requiredPermission = Permissions.ADMINISTRATOR
    argumentParams = listOf(
            TextToken("a setting to change") isOptional true isGreedy false
    )
    behaviour = { event, tokens ->

        val guildConfig = event.guild.getServerConfig()

        if(tokens.isEmpty()){

            event.channel.sendInfoEmbed(
                    header = "Settings",
                    bodyMessage =
                    "The settings are as follows (values delimited by []): \n" +
                            "${guildConfig.entries.fold("", { init, curObj ->
                                init + "`${curObj.key}` -> [${curObj.value}]\n"
                            })}\n" +
                            "To change any use `settings settingname` at which point you will be prompted to make a change"
            )

        } else {

            val settingName = tokens[0] as TextToken

            if(guildConfig.containsKey(settingName.underlyingString) && guildConfig[settingName.underlyingString] is String){

                val newSetting = event.channel.getUserResponse(
                        event.author,
                        header = "Change $settingName",
                        bodyMessage = "Currently the setting `$${settingName.underlyingString}` is set to `${guildConfig[settingName.underlyingString]}`\n" +
                                "Please enter the value you wish to change it to or press the X to cancel.\n"
                ) ?: guildConfig[settingName.underlyingString] as String

                AuditLog.log(
                        event.guild,
                        "User ${event.author.name} (${event.author.stringID}) has changed the setting of " +
                                "$settingName from `${guildConfig[settingName.underlyingString]}` to `$newSetting`"
                )

                guildConfig[settingName.underlyingString] = newSetting

                DiscordCore.updateGuildConfigStore(event.guild.stringID)

                event.message.indicateSuccess()

            } else {

                event.channel.sendErrorEmbed(errorMessage = "${settingName.underlyingString} is not a valid setting to change.")

            }

        }

    }
}

