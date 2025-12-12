package dev.lizainslie.cafemc.core.ext

import dev.lizainslie.cafemc.core.modules.OnlinePlayerCacheModule
import org.bukkit.entity.Player

val Player.realName get() = OnlinePlayerCacheModule[uniqueId]?.realName