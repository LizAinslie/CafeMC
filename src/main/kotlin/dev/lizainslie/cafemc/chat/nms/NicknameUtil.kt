package dev.lizainslie.cafemc.chat.nms

import dev.lizainslie.cafemc.chat.toPlainText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.reflect.Field
import com.mojang.authlib.GameProfile
import io.netty.buffer.Unpooled
import me.neznamy.tab.api.TabAPI
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.server.MinecraftServer
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.entity.Relative
import net.minecraft.world.level.GameType
import org.bukkit.entity.Entity
import java.lang.reflect.Constructor
import java.util.EnumSet
import java.util.UUID


object NicknameUtil {
    fun updateNickname(targetPlayer: Player, name: Component?) {
        setPlayerNameAboveHead(targetPlayer, name?.toPlainText() ?: targetPlayer.name)
        targetPlayer.displayName(name)
        setPlayerNameInTabList(targetPlayer, name)
    }

    fun setPlayerNameInTabList(player: Player, name: Component?) {
        val tab = TabAPI.getInstance()
        val tabPlayer = tab.getPlayer(player.uniqueId) ?: return

        (tab.tabListFormatManager ?: return).setName(
            tabPlayer,
            name?.let {
                LegacyComponentSerializer.legacyAmpersand().serialize(it)
            } ?: player.name
        )
    }

    /**
     * Using packets, sets the player's name above their head for everyone but
     * themselves. Caveats are this also affects tab completions and basically
     * every other instance in which the client handles a username. Let's hope
     * it doesn't blow up and let's pray that the gods forgive me for the crimes
     * which I have herein committed
     */
    @Suppress("UNCHECKED_CAST")
    fun setPlayerNameAboveHead(targetBukkit: Player, newName: String) {
        val mcServer: MinecraftServer = try {
            MinecraftServer.getServer()
        } catch (ex: Throwable) {
            // fallback to wack ass bukkit
            val serverObj = Bukkit.getServer()
            val serverField = serverObj.javaClass.getDeclaredField("console")
            serverField.isAccessible = true
            serverField.get(serverObj) as MinecraftServer
        }

        // get the NMS ServerPlayer
        val sp: ServerPlayer = mcServer.playerList.getPlayer(targetBukkit.uniqueId) ?: return

        // Replace GameProfile on the Player superclass (private final)
        try {
            val playerClass = net.minecraft.world.entity.player.Player::class.java
            val gpField: Field = playerClass.getDeclaredField("gameProfile").apply { isAccessible = true }

            val oldProfile = gpField.get(sp) as GameProfile
            val newProfile = GameProfile(targetBukkit.uniqueId, newName, oldProfile.properties)

            gpField.set(sp, newProfile)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        //
        // THE HORRORS BEGIN!!
        //

        // Build packets (use reflection fallback where necessary)
        val removeInfo = ClientboundPlayerInfoRemovePacket(listOf(targetBukkit.uniqueId))

        // PlayerInfoAdd - try convenient constructors first
        val addInfo: Packet<ClientGamePacketListener> = try {
            // try single-player ctor (Action, ServerPlayer)
            ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, sp)
        } catch (e: Throwable) {
            // try EnumSet + Collection constructor
            try {
                val enumSet = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)
                ClientboundPlayerInfoUpdatePacket(enumSet, listOf(sp))
            } catch (e2: Throwable) {
                // Last resort: create Entry records and use the (EnumSet, List<Entry>) constructor via reflection
                try {
                    val entryClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Entry")
                    val entryCtor = entryClass.getDeclaredConstructor(UUID::class.java, GameProfile::class.java, Boolean::class.javaPrimitiveType, Int::class.javaPrimitiveType, GameType::class.java, net.minecraft.network.chat.Component::class.java, Class.forName("net.minecraft.network.protocol.game.RemoteChatSession\$Data"))
                    entryCtor.isAccessible = true
                    val entry = entryCtor.newInstance(
                        targetBukkit.uniqueId,
                        sp.gameProfile, // mojmap my beloved <3
                        true,
                        targetBukkit.ping,
                        sp.gameMode,
                        null,
                        null
                    )
                    val enumSet = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)
                    val ctor = ClientboundPlayerInfoUpdatePacket::class.java.getDeclaredConstructor(EnumSet::class.java, java.util.List::class.java)
                    ctor.isAccessible = true
                    ctor.newInstance(enumSet, listOf(entry)) as Packet<ClientGamePacketListener>
                } catch (e3: Throwable) {
                    throw RuntimeException("Failed to construct ClientboundPlayerInfoUpdatePacket", e3)
                }
            }
        }

        // Remove entity packet
        val removeEntity = ClientboundRemoveEntitiesPacket(sp.id)

        // Add entity packet: players historically had a dedicated add-player packet, but on some builds that class/ctor is missing.
        // We'll try multiple options:
        val addEntityPacket: Packet<ClientGamePacketListener> = try {
            // Preferred convenience: AddEntity from Entity (works for non-player entities; player sometimes accepted)
            val ctor = Class.forName("net.minecraft.network.protocol.game.ClientboundAddEntityPacket").getDeclaredConstructor(Entity::class.java)
            ctor.isAccessible = true
            ctor.newInstance(sp) as Packet<ClientGamePacketListener>
        } catch (e: Throwable) {
            try {
                // Some builds expose a constructor with many explicit fields: (int id, UUID uuid, double x,double y,double z, float xRot, float yRot, EntityType, int data, Vec3 deltaMovement, float yHeadRot)
                val packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundAddEntityPacket")
                var chosenCtor: Constructor<*>? = null
                for (c in packetClass.declaredConstructors) {
                    val params = c.parameterTypes
                    if (params.size >= 10 && params[0] == Int::class.javaPrimitiveType) {
                        chosenCtor = c
                        break
                    }
                }
                if (chosenCtor == null) throw NoSuchMethodException("no add-entity ctor")

                chosenCtor.isAccessible = true
                val entityType = sp.type // net.minecraft.world.entity.EntityType
                val motion = sp.deltaMovement
                val instance = chosenCtor.newInstance(
                    sp.id,
                    sp.uuid,
                    sp.x,
                    sp.y,
                    sp.z,
                    sp.xRot,
                    sp.yRot,
                    entityType,
                    0,
                    motion,
                    sp.yHeadRot
                )
                instance as Packet<ClientGamePacketListener>
            } catch (e2: Throwable) {
                // final fallback: construct via a FriendlyByteBuf-backed constructor if present
                try {
                    val packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundAddEntityPacket")
                    val bufCtor = packetClass.declaredConstructors.find { it.parameterTypes.size == 1 }
                        ?: throw RuntimeException("No single-arg ctor on ClientboundAddEntityPacket")
                    bufCtor.isAccessible = true

                    val fBuf = FriendlyByteBuf(Unpooled.buffer())
                    fBuf.writeInt(sp.id)
                    fBuf.writeUUID(sp.uuid)
                    fBuf.writeDouble(sp.x)
                    fBuf.writeDouble(sp.y)
                    fBuf.writeDouble(sp.z)
                    fBuf.writeFloat(sp.xRot)
                    fBuf.writeFloat(sp.yRot)
                    val typeKey = sp.type.builtInRegistryHolder().key().location()
                    fBuf.writeResourceLocation(typeKey)
                    // data int
                    fBuf.writeInt(0)
                    // motion vec3
                    fBuf.writeDouble(sp.deltaMovement.x)
                    fBuf.writeDouble(sp.deltaMovement.y)
                    fBuf.writeDouble(sp.deltaMovement.z)
                    fBuf.writeFloat(sp.yHeadRot)
                    // finally construct
                    bufCtor.newInstance(fBuf) as Packet<ClientGamePacketListener>
                } catch (e3: Throwable) {
                    throw RuntimeException("Cannot build ClientboundAddEntityPacket for this server build", e3)
                }
            }
        }

        // Teleport packet: try Entity ctor, then int-based ctor, then the ctor with PositionMoveRotation & EnumSet<Relative>
        val teleportPacket: Packet<ClientGamePacketListener> = try {
            val teleCtor = Class.forName("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket").getDeclaredConstructor(
                Entity::class.java)
            teleCtor.isAccessible = true
            teleCtor.newInstance(sp) as Packet<ClientGamePacketListener>
        } catch (e: Throwable) {
            try {
                // constructor with explicit fields: (int id, double x,double y,double z, byte yRot, byte xRot, boolean onGround)
                val teleClass = Class.forName("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket")
                val found = teleClass.declaredConstructors.find { it.parameterTypes.size >= 1 && it.parameterTypes[0] == Int::class.javaPrimitiveType }
                    ?: throw NoSuchMethodException("no int ctor")
                found.isAccessible = true
                // convert rotations
                val yRotByte = ((sp.yRot % 360f) * 256f / 360f).toInt().toByte()
                val xRotByte = ((sp.xRot % 360f) * 256f / 360f).toInt().toByte()
                found.newInstance(sp.id, sp.x, sp.y, sp.z, yRotByte, xRotByte, sp.onGround) as Packet<ClientGamePacketListener>
            } catch (e2: Throwable) {
                try {
                    val change = PositionMoveRotation(
                        sp.position(),
                        sp.deltaMovement,
                        sp.yRot,
                        sp.xRot
                    )
                    ClientboundTeleportEntityPacket(
                        sp.id,
                        change,
                        EnumSet.noneOf(Relative::class.java),
                        sp.onGround
                    )
                } catch (e3: Throwable) {
                    throw RuntimeException("Cannot build teleport packet for this server build", e3)
                }
            }
        }

        // Send the packets to every player
        for (viewer in mcServer.playerList.players) {
            try {
                if (viewer == sp) {
                    // viewer is self, skip packets
                    continue
                }
                viewer.connection.send(removeInfo)
                viewer.connection.send(removeEntity)
                viewer.connection.send(addInfo)
                viewer.connection.send(addEntityPacket)
                viewer.connection.send(teleportPacket)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}