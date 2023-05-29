package com.github.jsh32.astroisles.common

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.BukkitCommandManager
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor
import cloud.commandframework.kotlin.coroutines.annotations.installCoroutineSupport
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import kotlin.jvm.optionals.getOrDefault

/**
 * Command manager based around the Cloud Command Framework.
 */
class CommandManager(plugin: JavaPlugin, private val audience: BukkitAudiences, name: String, private val prefix: String) {
    private val manager: BukkitCommandManager<CommandSender> = run {
        val executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.builder<CommandSender>().build()
        val mapperFunction: java.util.function.Function<CommandSender, CommandSender> = java.util.function.Function.identity()
        val manager = PaperCommandManager(
            plugin,
            executionCoordinatorFunction,
            mapperFunction,
            mapperFunction
        )

        manager.commandSuggestionProcessor(
            FilteringCommandSuggestionProcessor(
                FilteringCommandSuggestionProcessor.Filter.contains<CommandSender>(true).andTrimBeforeLastSpace()
            )
        )

        // Register Brigadier mappings
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier()
        }

        // Register asynchronous completions
        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (manager as PaperCommandManager<*>).registerAsynchronousCompletions()
        }

        manager
    }

    private var minecraftHelp = MinecraftHelp(
        "/$prefix help", { sender: CommandSender? -> audience.sender(sender!!) },
        manager
    )

    private var annotationParser = run {
        val commandMetaFunction: java.util.function.Function<ParserParameters, CommandMeta> =
            java.util.function.Function<ParserParameters, CommandMeta> { p ->
                CommandMeta.simple() // This will allow you to decorate commands with descriptions
                    .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                    .build()
            }

        val parser = AnnotationParser(
            manager,
            CommandSender::class.java,
            commandMetaFunction
        )

        // Enable kotlin coroutine support.
        parser.installCoroutineSupport()

        parser
    }

    init {
        MinecraftExceptionHandler<CommandSender>()
            .withInvalidSyntaxHandler()
            .withInvalidSenderHandler()
            .withNoPermissionHandler()
            .withArgumentParsingHandler()
            .withCommandExecutionHandler()
            .withDecorator { component: Component? ->
                Component.text()
                    .append(Component.text("[", NamedTextColor.DARK_GRAY))
                    .append(Component.text(name, NamedTextColor.GOLD))
                    .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                    .append(component!!).build()
            }.apply(manager) { sender: CommandSender? ->
                audience.sender(sender!!)
            }

        defaultCommands()
    }

    private fun defaultCommands() {
        val builder = manager.commandBuilder(prefix)
        manager.command(builder.literal("help")
            .meta(CommandMeta.DESCRIPTION, "Help menu")
            .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
            .handler { ctx ->
                val query = ctx.getOptional<String>("query")
                this.minecraftHelp.queryCommands(query.getOrDefault(""), ctx.sender)
            })
    }

    /**
     * Register commands on a class
     */
    fun registerCommands(instance: Any) {
        annotationParser.parse(instance)
    }
}