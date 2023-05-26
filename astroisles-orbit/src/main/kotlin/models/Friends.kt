package models

import io.ebean.DB
import io.ebean.annotation.DbComment
import io.ebean.annotation.Index
import models.query.QFriends
import java.util.UUID
import javax.persistence.Entity

/**
 * A friend request. Two users are friends when this relationship exists with an inverse as well.
 */
@Entity
@Index(
    unique = true, columnNames = ["from", "to"]
)
class Friends(
    @DbComment("User who requested to be friends.")
    val from: UUID,
    @DbComment("User where request was sent")
    val to: UUID
) : BaseModel() {
    companion object {
        /**
         * Check if two users are mutually friends.
         */
        fun areFriends(user1: UUID, user2: UUID): Boolean {
            val count = QFriends().where()
                .from.eq(user1)
                .to.eq(user2)
                .or()
                    .from.eq(user2)
                    .and()
                        .to.eq(user1)
                    .endAnd()
                .endOr()
                .findCount()

            return count == 2
        }
    }
}