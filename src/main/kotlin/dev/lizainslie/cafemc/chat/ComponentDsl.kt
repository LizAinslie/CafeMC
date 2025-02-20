package dev.lizainslie.cafemc.chat

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.meta.ItemMeta

class ComponentDsl {
    var component = Component.empty()
        private set

    fun text(text: String, opts: TextOptionsDsl.() -> Unit = {}) {
        val c = Component.text(text)
        component = component.append(TextOptionsDsl(c).apply(opts).build())
    }
    
    fun text(component: Component, opts: TextOptionsDsl.() -> Unit = {}) {
        this.component = this.component.append(TextOptionsDsl(component).apply(opts).build())
    }
    
    fun newline() {
        component = component.append(Component.newline())
    }
    
    fun space() {
        component = component.append(Component.space())
    }

    class TextOptionsDsl(private var component: Component) {
        var bold = false
        var italic = false
        var underlined = false
        var strikethrough = false
        var obfuscated = false

        var color: TextColor? = null
        
        private var eventOptionsBuilder: (EventOptionsDsl.() -> Unit)? = null

        fun events(opts: EventOptionsDsl.() -> Unit) {
            eventOptionsBuilder = opts
        }

        fun build(): Component {
            component = component.color(color)
            component = component.decoration(TextDecoration.BOLD, bold)
            component = component.decoration(TextDecoration.ITALIC, italic)
            component = component.decoration(TextDecoration.UNDERLINED, underlined)
            component = component.decoration(TextDecoration.STRIKETHROUGH, strikethrough)
            component = component.decoration(TextDecoration.OBFUSCATED, obfuscated)


            eventOptionsBuilder?.let { component = EventOptionsDsl(component).applyComponent(it) }
            
            return component
        }
    }

    class EventOptionsDsl(private var component: Component) {
        var hoverEventSource: HoverEventSource<*>? = null
        var click: ClickEvent? = null
        
        fun hover(source: HoverEventSource<*>) {
            hoverEventSource = source
        }
        
        fun hover(block: ComponentDsl.() -> Unit) {
            hoverEventSource = ComponentDsl().apply(block).component
        }

        fun applyComponent(block: EventOptionsDsl.() -> Unit): Component {
            apply(block)
            
            hoverEventSource?.let { component = component.hoverEvent(hoverEventSource) }
            click?.let { component = component.clickEvent(it) }
            
            return component
        }
    }
}

fun component(block: ComponentDsl.() -> Unit): Component {
    return ComponentDsl().apply(block).component
}

fun Audience.sendRichMessage(block: ComponentDsl.() -> Unit) {
    sendMessage(component(block))
}

fun ItemMeta.setRichDisplayName(block: ComponentDsl.() -> Unit) {
    displayName(component(block))
}

fun ItemMeta.addRichLoreLine(block: ComponentDsl.() -> Unit) {
    val lore = lore() ?: mutableListOf<Component>()
    lore += component(block)
    lore(lore)
}