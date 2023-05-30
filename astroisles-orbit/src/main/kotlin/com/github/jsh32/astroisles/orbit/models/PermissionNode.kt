package com.github.jsh32.astroisles.orbit.models

import io.ebean.annotation.DbComment
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class PermissionNode(
    @DbComment("Actual permission node.")
    val node: String,
    @DbComment("Rank that this node belongs to.")
    @ManyToOne
    val rank: Rank
) : BaseModel()