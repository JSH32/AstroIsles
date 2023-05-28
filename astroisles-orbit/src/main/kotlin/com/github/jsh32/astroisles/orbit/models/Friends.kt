package com.github.jsh32.astroisles.orbit.models

import com.github.jsh32.astroisles.orbit.models.query.QFriends
import io.ebean.annotation.DbComment
import io.ebean.annotation.Index
import java.util.*
import javax.persistence.Entity

/**
 * A friend request. Two users are friends when this relationship exists with an inverse as well.
 */
@Entity
@Index(
    unique = true, columnNames = ["from_user", "to_user"]
)
class Friends(
    @DbComment("User who requested to be friends.")
    val fromUser: UUID,
    @DbComment("User where request was sent.")
    val toUser: UUID
) : BaseModel() {
    companion object {
        /**
         * Check if two users are mutually friends.
         */
        fun areFriends(user1: UUID, user2: UUID): Boolean {
            val count = QFriends().where()
                .fromUser.eq(user1)
                .toUser.eq(user2)
                .or()
                    .fromUser.eq(user2)
                    .and()
                        .toUser.eq(user1)
                    .endAnd()
                .endOr()
                .findCount()

            return count == 2
        }
    }
}