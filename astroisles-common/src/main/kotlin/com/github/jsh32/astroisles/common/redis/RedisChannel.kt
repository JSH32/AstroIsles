package com.github.jsh32.astroisles.common.redis

/**
 * Mark classes as being able to be sent/received to Jedis.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class RedisChannel(val channel: String)

