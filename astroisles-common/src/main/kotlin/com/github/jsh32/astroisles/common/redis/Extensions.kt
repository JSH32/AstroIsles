package com.github.jsh32.astroisles.common.redis

import com.google.gson.Gson
import redis.clients.jedis.Jedis

/**
 * Get and deserialize a redis key value.
 */
inline fun <reified T> Jedis.getObject(key: String): T? {
    val value = get(key)
    return if (value != null) {
        Gson().fromJson(value, T::class.java)
    } else {
        null
    }
}

/**
 * Publish a value to Jedis. It must be annotated with [RedisChannel].
 */
fun <T : Any> Jedis.publishChannel(value: T) {
    val channelAnnotation = value.javaClass.getAnnotation(RedisChannel::class.java)
    if (channelAnnotation != null) {
        publish(channelAnnotation.channel, Gson().toJson(value))
    } else {
        throw IllegalArgumentException("${value.javaClass.name} isn't annotated with RedisChannel")
    }
}
