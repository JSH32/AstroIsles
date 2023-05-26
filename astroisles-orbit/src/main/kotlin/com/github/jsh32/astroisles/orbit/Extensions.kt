package com.github.jsh32.astroisles.orbit

import com.google.protobuf.TimestampKt
import java.sql.Timestamp

fun TimestampKt.fromSql(timestamp: Timestamp): com.google.protobuf.Timestamp = com.google.protobuf.timestamp {
    val instant = timestamp.toInstant()
    seconds = instant.epochSecond
    nanos = instant.nano
}