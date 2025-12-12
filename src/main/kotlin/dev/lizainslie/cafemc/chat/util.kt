package dev.lizainslie.cafemc.chat

import net.kyori.adventure.text.Component

fun validateNickname(nickname: Component): Boolean {
    return (nickname.toPlainText().length <= 16)
}