package dev.lizainslie.cafemc.chat

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ItemTag
import net.md_5.bungee.api.chat.hover.content.Content
import net.md_5.bungee.api.chat.hover.content.Entity
import net.md_5.bungee.api.chat.hover.content.Item
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import org.bukkit.entity.Entity as BukkitEntity
import org.bukkit.inventory.ItemStack

class MessageBuilderDsl {
    private var componentBuilder = ComponentBuilder()
    
    fun text(text: String, opts: TextOptionsDsl.() -> Unit = {}) {
        componentBuilder = componentBuilder.append(text)
        componentBuilder = TextOptionsDsl(componentBuilder).apply(opts).componentBuilder
        println("text: $text added")
    }
    
    fun create(): Array<BaseComponent> = componentBuilder.create()
    fun build(): BaseComponent = componentBuilder.build()
    
    class TextOptionsDsl(var componentBuilder: ComponentBuilder) {
        var bold: Boolean get() = componentBuilder.currentComponent.isBold
            set(value) { componentBuilder = componentBuilder.bold(value) }
        var italic: Boolean get() = componentBuilder.currentComponent.isItalic
            set(value) { componentBuilder = componentBuilder.italic(value) }
        var underlined: Boolean get() = componentBuilder.currentComponent.isUnderlined
            set(value) { componentBuilder = componentBuilder.underlined(value) }
        var strikethrough: Boolean get() = componentBuilder.currentComponent.isStrikethrough
            set(value) { componentBuilder = componentBuilder.strikethrough(value) }
        var obfuscated: Boolean get() = componentBuilder.currentComponent.isObfuscated
            set(value) { componentBuilder = componentBuilder.obfuscated(value) }
        
        var color: ChatColor? get() = componentBuilder.currentComponent.color
            set(value) { componentBuilder = componentBuilder.color(value) }
        
        fun reset() {
            bold = false
            italic = false
            underlined = false
            strikethrough = false
            obfuscated = false
            color = null
            
            componentBuilder = componentBuilder.reset()
        }
        
        fun events(opts: EventOptionsDsl.() -> Unit) {
            componentBuilder = EventOptionsDsl(componentBuilder).applyComponent(opts)
        }
    }
    
    class EventOptionsDsl(private var componentBuilder: ComponentBuilder) {
        fun hover(opts: HoverEventDsl.() -> Unit) {
            componentBuilder = HoverEventDsl(componentBuilder).applyComponent(opts)
        }

        fun click(opts: ClickEventDsl.() -> Unit) {
            componentBuilder = ClickEventDsl(componentBuilder).applyComponent(opts)
        }
        
        fun applyComponent(block: EventOptionsDsl.() -> Unit) = apply(block).componentBuilder
        
        class HoverEventDsl(private var componentBuilder: ComponentBuilder) {
            private var action: HoverEvent.Action? = null
            private var content: Content? = null

            private fun content(content: Content) {
                this.content = content
            }

            fun textContent(text: String) {
                action = HoverEvent.Action.SHOW_TEXT
                content(Text(text))
            }
            
            fun textContent(contentBuilder: MessageBuilderDsl.() -> Unit) {
                action = HoverEvent.Action.SHOW_TEXT
                content(Text(MessageBuilderDsl().apply(contentBuilder).create()))
            }
            
            fun entityContent(type: String, id: String, name: String) {
                action = HoverEvent.Action.SHOW_ENTITY
                content(Entity(type, id, ComponentBuilder(name).build()))
            }
            
            fun entityContent(type: String, id: String, nameBuilder: MessageBuilderDsl.() -> Unit) {
                action = HoverEvent.Action.SHOW_ENTITY
                content(Entity(type, id, MessageBuilderDsl().apply(nameBuilder).build()))
            }
            
            fun entityContent(entity: BukkitEntity) {
                entityContent(entity.type.name, entity.uniqueId.toString(), entity.name)
            }
            
            fun itemContent(id: String, count: Int, tag: ItemTag? = null) {
                action = HoverEvent.Action.SHOW_ITEM
                content(Item(id, count, tag))
            }
            
            fun itemContent(itemStack: ItemStack) {
                itemContent(itemStack.type.key.toString(), itemStack.amount, itemStack.itemMeta?.let { ItemTag.ofNbt(it.asComponentString) })
            }
            
            fun applyComponent(block: HoverEventDsl.() -> Unit): ComponentBuilder {
                apply(block)
                
                if (action != null && content != null) componentBuilder = componentBuilder.event(
                    HoverEvent(
                        action,
                        content
                    )
                )
                
                println("hover event: $action, $content applied")
                
                return componentBuilder
            }
        }
        
        class ClickEventDsl(private var componentBuilder: ComponentBuilder) {
            var action: ClickEvent.Action? = null
            var value: String? = null
            
            fun applyComponent(block: ClickEventDsl.() -> Unit): ComponentBuilder {
                apply(block)
                
                if (action != null && value != null) componentBuilder = componentBuilder.event(
                    ClickEvent(
                        action,
                        value
                    )
                )
                
                println("click event: $action, $value applied")
                
                return componentBuilder
            }
        }
    }
}

fun Player.sendMessage(block: MessageBuilderDsl.() -> Unit) {
    spigot().sendMessage(MessageBuilderDsl().apply(block).build())
}