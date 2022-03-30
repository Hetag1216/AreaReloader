# AreaReloader
![Core Icon](https://media.discordapp.net/attachments/595194807932944385/614115793382146058/AR.png)

An easy to use Spigot plugin to roll back areas at the stage they were saved!

![Core Icon](https://cdn.discordapp.com/attachments/595194807932944385/614124252387606565/ARCommands.png)

/ar - Shows plugin's help lines

/ar help <Command> - Shows general help or specific help when a command is spefied

/ar version - Shows the current plugin's version

/ar create <AreaName> <CopyEntities> - Creates a new copy of the selected area.
The copy entities parameter will accept a true or false value, which will decide whether or not to copy entities inside the selected area at the moment of creation.
If set to true, whenever the area gets loaded, saved entities will be respawned.

/ar load <AreaName> - Loads an existing area

/ar delete <AreaName> - Deletes an existing area

/ar list - Lists all existing areas

/ar hook - Shows a help interface for the plugin's hooks and dependencies

/ar info - Shows information about a specific existing area

/ar reload - Reloads AreaReloader's configuration file

/ar display <AreaName> - Displays particles around an area

/ar cancel <AreaName, ALL> - Cancels the loading of one or all areas.

![Core Icon](https://cdn.discordapp.com/attachments/595194807932944385/614124189229514772/ARPermissions.png)

areareloader.command.help - Gives access to the /ar help command

areareloader.command.version - Gives access to the /ar version command

areareloader.command.create - Gives access to the /ar create command

areareloader.command.load - Gives access to the /ar load command

areareloader.command.delete - Gives access to the /ar delete command

areareloader.command.list - Gives access to the /ar list command

areareloader.command.hook - Gives access to the /ar hook command

areareloader.command.info - Gives access to the /ar info command

areareloader.command.reload - Gives access to the /ar reload command

areareloader.command.cancel - Gives access to /ar cancel command

areareloader.command.admin - Gives access to all commands

![Core Icon](https://i.imgur.com/v2uHY9wh.png)

The main configuration file where you will be able to edit your own language settings is named as "config.yml".
The config is very easy to understand as it is composed by regular language strings, however, the plugin brings some variables and new configuration settings that affect the plugin.

~~---------------------------------------------------------------------------------------------------------------------------------~~

*Guide to debugging*: 
Open a new issue in the [Issues category](https://github.com/Hetag1216/AreaReloader/issues)
When opening a new issue please add and specify:
  - Debug's file (located at AreaReloader\debug.txt).
  - Spigot's version - /version;
  - AreaReloader's version - /ar version;
  - WorldEdit's version;
  - Where/when you met the issue, as in what action had been fired at the time (command, auto reloading, etc.).
  - Console's logs (specifically error's stack trace if not available in debug.txt).

The debug logs will be printed to the command sender.
Default value: false (turn 'false' to true and then restart the server to enable the debugging or any config changes)

~~---------------------------------------------------------------------------------------------------------------------------------~~

Area Interval: This represents the interval between each area's section being loaded.
The interval must be specified in milliseconds.
Depending on your server's resources and performance you may lower or raise this.
Smaller interval values may affect your server's performance negatively.
Default value: 500 (0.5 seconds)

~~---------------------------------------------------------------------------------------------------------------------------------~~

AreaScheduler: This function allows you to automatically reload your areas after a given interval, experessed in milliseconds.
To enable an area to automatically reload navigate through your areas' configuration file (path: AreaReloader\areas.yml), under the AutoReload section of your area of interest change the Enabled status from "false" to "true", reload your plugin/server and enjoy your areas get loaded and built just like magic.

~~---------------------------------------------------------------------------------------------------------------------------------~~

Metrics: this functionality provides data which helps me keep track of the plugin's usage.
I invite you to keep this setting on as it contributes to boosting my dedication and work towards my projects!
Provided by: bStats.


Join now the discord for live support and live updates on the projects!
![Core Icon](https://cdn.discordapp.com/attachments/595364073147728025/687819024457007140/discord_header.png)
https://discord.gg/yqs9UJs
