package com.github.jsh32.astroisles.orbit.models

import io.ebean.annotation.DbComment
import java.sql.Timestamp
import java.util.*
import javax.persistence.Entity

/**
 * A saved player.
 */
@Entity
class Player(
    @DbComment("Player UUID.")
    val playerId: UUID,
    @DbComment("Player username.")
    val playerName: String,
    @DbComment("Time the player first joined.")
    val firstJoin: Timestamp,
    @DbComment("Last time the user logged out.")
    val lastLogout: Timestamp? = null
) : BaseModel()