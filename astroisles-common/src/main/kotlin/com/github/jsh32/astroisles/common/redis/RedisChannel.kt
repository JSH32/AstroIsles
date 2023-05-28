package com.github.jsh32.astroisles.common.redis

import com.google.gson.Gson
import redis.clients.jedis.Jedis

/**
 * Mark classes as being able to be sent/received to Jedis.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class RedisChannel(val channel: String)

