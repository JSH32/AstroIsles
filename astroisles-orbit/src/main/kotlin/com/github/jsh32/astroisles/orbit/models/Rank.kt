package com.github.jsh32.astroisles.orbit.models

import io.ebean.annotation.DbComment
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * A rank (or role).
 */
@Entity
@Table(name = "ranks")
class Rank(
    @DbComment("Rank name")
    val name: String,
    @DbComment("Rank hex color")
    val color: String,
    @DbComment("Rank display name")
    val displayName: String,
    @OneToMany(mappedBy = "ranks", cascade = [CascadeType.REMOVE])
    val nodes: MutableList<PermissionNode> = mutableListOf()
) : BaseModel()