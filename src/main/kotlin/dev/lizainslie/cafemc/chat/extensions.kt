package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.data.player.PlayerSettings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

fun Collection<Component>.joinToComponent(separator: Component = Component.space()): Component {
    var c = Component.empty()
    
    forEachIndexed { index, component ->
        c = c.append(component)
        
        if (index < size - 1) c = c.append(separator)
    }
    
    return c
}

fun nicknameOrDisplayName(uuid: UUID, defaultName: Component): Component {
    val nickname = PlayerSettings.find(uuid)?.nickname

    return nickname?.let {
        component {
            text(ChatUtil.translateAmpersand(it)) {
                events {
                    hover {
                        text("Username: ") { color = NamedTextColor.GRAY }
                        text(defaultName) { color = NamedTextColor.AQUA }
                    }
                }
            }
        }
    } ?: defaultName
}

fun Player.nicknameOrDisplayName() = nicknameOrDisplayName(
    uuid = this.uniqueId,
    defaultName = this.displayName()
)

fun OfflinePlayer.nicknameOrDisplayName() = nicknameOrDisplayName(
    uuid = this.uniqueId,
    defaultName = Component.text(this.name ?: "Unknown")
)