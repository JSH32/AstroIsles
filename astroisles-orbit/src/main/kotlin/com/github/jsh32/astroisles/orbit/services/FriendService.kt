package com.github.jsh32.astroisles.orbit.services

import FriendServiceGrpcKt
import friendRequestReply

class FriendService : FriendServiceGrpcKt.FriendServiceCoroutineImplBase() {
    override suspend fun addFriend(request: FriendServiceOuterClass.FriendRequest): FriendServiceOuterClass.FriendRequestReply {
        return friendRequestReply {
            status = FriendServiceOuterClass.FriendRequestReply.Status.AlreadyFriends
        }
    }
}