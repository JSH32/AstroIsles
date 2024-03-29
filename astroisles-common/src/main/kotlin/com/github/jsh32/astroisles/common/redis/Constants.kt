package com.github.jsh32.astroisles.common.redis

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

    @RedisChannel("player_quit")
    class PlayerQuit(val playerId: String)

    @RedisChannel("player_chat")
    class PlayerChat(val playerId: String, val message: String)
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

