package dev.lizainslie.cafemc.chat

import net.kyori.adventure.text.Component

fun Collection<Component>.joinToComponent(separator: Component = Component.space()): Component {
    var c = Component.empty()
    
    forEachIndexed { index, component ->
        c = c.append(component)
        
        if (index < size - 1) c = c.append(separator)
    }
    
    return c
}