package com.github.jsh32.astroisles.common.redis

import com.google.gson.Gson
import java.lang.reflect.ParameterizedType

/**
 * Redis databases used within AstroIsles.
 */
object RedisDatabases {
    const val PLAYER_SERVER = 0
}

/**
 * All messages that can be published or subscribed.
 */
object RedisMessages {
    @RedisChannel("player_join")
    class PlayerJoin(val playerId: String, val serverId: String, val firstJoin: Boolean, val initialJoin: Boolean)
}

/**
 * Objects that can be stored within redis.
 */
object RedisObjects {
    /**
     * In [RedisDatabases.PLAYER_SERVER].
     */
    class PlayerServerData(val serverId: String)
}

