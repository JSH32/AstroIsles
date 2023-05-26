package com.github.jsh32.astroisles.core.modules

import FriendRequestKt
import FriendServiceGrpcKt
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import com.github.jsh32.astroisles.common.module.Module
import com.google.inject.Inject
import com.google.inject.Singleton
import friendRequest
import io.grpc.ManagedChannel
import org.bukkit.command.CommandSender
import java.util.*


//@Config(file = "friend.conf")
//@ConfigSerializable
//private class TestConfig {
//    @Setting(value = "player")
//    val player: String = "hello"
//
//    @Setting(value = "ok")
//    @Comment("Is the player good")
//    val ok: Boolean = true
//}

@Singleton
class FriendModule @Inject constructor(
    orbitClient: ManagedChannel
) : Module("FriendModule") {
    private val friendService = FriendServiceGrpcKt.FriendServiceCoroutineStub(orbitClient)

    override fun enable() {
//        println(loadConfig<TestConfig>()!!.player)
    }

    @CommandDescription("Test cloud command using @CommandMethod")
    @CommandMethod("friend")
    suspend fun friendCommand(sender: CommandSender) {
        val response = friendService.addFriend(friendRequest {
            receiver = "Test"
            senderUuid = "Test"
        })

        sender.sendMessage(response.status.name)
    }
}