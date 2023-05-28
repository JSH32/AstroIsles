package com.github.jsh32.astroisles.common.redis

import com.google.gson.Gson
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.lang.reflect.ParameterizedType

/**
 * A utility wrapper around a jedis subscriber thread.
 */
class JedisListener(private val pool: JedisPool) {
    private val listeners = mutableMapOf<String, MutableList<ChannelListener<*>>>()
    private var thread: JedisThread? = null
    private val listener = MessageListener(listeners)
    // Should the thread be started.
    // This exists because a subscription cannot exist with no channels so we should turn on the thread when first subscribed.
    private var shouldBeStarted = false

    private class JedisThread(
        val pool: JedisPool,
        val messageListener: MessageListener,
        val channels: List<String>
    ) : Thread() {
        override fun run() {
            pool.resource.subscribe(messageListener, *channels.toTypedArray())
        }
    }

    private class MessageListener(val listeners: Map<String, List<ChannelListener<*>>>) : JedisPubSub() {
        override fun onMessage(channel: String?, message: String?) {
            listeners[channel]?.let {
                for (listener in it) { listener.onMessage(message!!) }
            }
        }
    }

    fun <T : ChannelListener<*>> addListener(listener: T) {
        val list = listeners[listener.channel]
        if (list == null) {
            listeners[listener.channel] = mutableListOf(listener)
            // A new channel has been added. We need to restart the listener.
            if (this.listener.isSubscribed)
                this.listener.unsubscribe()
            if (shouldBeStarted) start()
        } else {
            if (list.contains(listener)) {
                throw IllegalArgumentException("Listener is already present.")
            } else {
                list.add(listener)
                if (shouldBeStarted) start()
            }
        }
    }

    fun start() {
        if (thread == null || !thread!!.isAlive) {
            shouldBeStarted = true
            val channels = listeners.keys.toList()
            if (channels.isNotEmpty()) {
                thread = JedisThread(pool, listener, listeners.keys.toList())
                thread!!.start()
            }
        } else {
            throw IllegalStateException("PubSub listener is already started.")
        }
    }

    /**
     * Safely stop the subscriptions on this listener.
     * This will stop the pubsub thread.
     */
    fun stop() {
        if (thread != null && thread!!.isAlive) {
            shouldBeStarted = false
            if (this.listener.isSubscribed)
                this.listener.unsubscribe()
        } else {
            throw IllegalStateException("PubSub listener is already stopped.")
        }
    }
}


abstract class ChannelListener<T> {
    private val gson = Gson()
    private val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
    val channel: String

    init {
        val annotation = (type as Class<*>).getAnnotation(RedisChannel::class.java)
        if (annotation != null) {
            channel = annotation.channel
        } else {
            throw IllegalStateException("${type.name} isn't annotated with RedisChannel")
        }
    }

    fun onMessage(message: String) {
        onMessage(gson.fromJson<T>(message, type))
    }

    /**
     * Message received on channel. It will attempt to parse message as the generic type parameter.
     */
    abstract fun onMessage(message: T)
}

