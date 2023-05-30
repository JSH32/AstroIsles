package com.github.jsh32.astroisles.common.redis

import com.google.gson.Gson
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * A utility for redis pubsub.
 * This class provides a simple wrapper to manage subscriptions and callbacks for Redis channels.
 */
class JedisListener(private val pool: JedisPool) {
    private var thread: JedisThread? = null
    private val listeners = mutableListOf<ChannelListener>()
    private val listener = MessageListener(listeners)
    // Should the thread be started.
    // This exists because a subscription cannot exist with no channels, so we should turn on the thread when first subscribed.
    private var shouldBeStarted = false

    var subscribed = false
        private set

    /**
     * A thread that handles subscribing to Redis channels with a [MessageListener].
     */
    private class JedisThread(
        val pool: JedisPool,
        val messageListener: MessageListener,
        val channels: List<String>
    ) : Thread() {
        override fun run() {
            pool.resource.use {
                it.subscribe(messageListener, *channels.toTypedArray())
            }
        }
    }

    /**
     * [MessageListener] is passed to Jedis and manages incoming messages.
     * @property listeners [ChannelListener]'s to dispatch events to.
     */
    private class MessageListener(val listeners: List<ChannelListener>) : JedisPubSub() {
        override fun onMessage(channel: String, message: String) {
            listeners
                .filter { it.channels.contains(channel) }
                .forEach { it.onMessage(channel, message) }
        }
    }

    /**
     * Adds a new [ChannelListener] and subscribes to its channels.
     * @param listener [ChannelListener] to add.
     * @throws IllegalArgumentException if the listener is already present.
     */
    fun addListener(listener: ChannelListener) {
        if (listeners.find { listener === it } != null) {
            throw IllegalArgumentException("Listener is already present.")
        } else {
            val channels = listeners.map { it.channels }.flatten().toSet()
            val channelsNotAdded = listener.channels.all { channels.contains(it) }
            listeners.add(listener)
            if (!channelsNotAdded) {
                // A new channel has been added. We need to restart the listener.
                unsubscribe()
            }

            // Start if not started already.
            if (shouldBeStarted && !subscribed) start()
        }
    }

    fun removeListener(listener: ChannelListener) {
        val removed = listeners.removeIf { listener === it }
        if (removed) {
            stop()
            start()
        }
    }

    private fun unsubscribe() {
        subscribed = false
        if (this.listener.isSubscribed)
            this.listener.unsubscribe()
    }

    /**
     * Starts the subscriber thread that manages the subscriptions for the listeners.
     * NOTE: This doesn't start immediately if there are no added listeners since at least one subscription is needed.
     * It will flag the thread for starting, and it will be started when the first listener is added.
     *
     * @throws IllegalStateException if the pubsub listener is already started.
     */
    fun start() {
        if (!subscribed || !shouldBeStarted) {
            shouldBeStarted = true
            subscribed = true
            if (listeners.isNotEmpty()) {
                val channels = listeners.flatMap { it.channels }.toSet().toList()
                thread = JedisThread(pool, listener, channels)
                thread!!.start()
            }
        } else {
            throw IllegalStateException("PubSub listener is already started.")
        }
    }

    /**
     * Safely stop the subscriptions on this listener.
     * This will stop the pubsub thread.
     * @throws IllegalStateException if the pubsub listener is already stopped.
     */
    fun stop() {
        if (subscribed) {
            shouldBeStarted = false
            unsubscribe()
        } else {
            throw IllegalStateException("PubSub listener is already stopped.")
        }
    }
}

/**
 * An abstract listener for managing Redis callbacks.
 * @param listeners list of message classes to subscribe to. These must all have the [RedisChannel] annotation.
 */
abstract class ChannelListener(vararg listeners: KClass<*>) {
    val channels: List<String>
    private val gson = Gson()
    private val listenerClasses = mutableMapOf<String, Class<*>>()
    private val handlers = mutableMapOf<String, (Any) -> Unit>()

    init {
        val listenerChannels = mutableListOf<String>()
        for (listener in listeners) {
            val annotation = listener.java.getAnnotation(RedisChannel::class.java)
            if (annotation != null) {
                listenerClasses[annotation.channel] = listener.java
                listenerChannels.add(annotation.channel)
            } else {
                throw IllegalStateException("${listener.java.name} isn't annotated with RedisChannel")
            }
        }

        channels = listenerChannels
    }

    /**
     * Dispatch a message to the listener and properly route it to the handler.
     */
    internal fun onMessage(channel: String, message: String) {
        listenerClasses[channel]?.let { clazz ->
            handlers[channel]?.invoke(gson.fromJson(message, clazz as Type))
        }
    }

    /**
     * Registers a callback to handle messages received from a type (annotated with [RedisChannel].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> handle(type: Class<T>, handler: (T) -> Unit) {
        listenerClasses.values.find { it == type }?.getAnnotation(RedisChannel::class.java)?.channel?.let {
            handlers[it] = handler as (Any) -> Unit
        } ?: throw IllegalArgumentException("${type.name} is not in the listeners list.")
    }

    /**
     * Registers a callback to handle messages received from a type (annotated with [RedisChannel].
     */
    inline fun <reified T> handle(noinline handler: (T) -> Unit) = handle(T::class.java, handler)
}

