package dev.lizainslie.cafemc.util

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.temporal.TemporalAccessor

object DiscordUtils {
    fun buildEmbed(block: EmbedBuilderDsl.() -> Unit) =
        EmbedBuilderDsl().apply(block).build()
}

class EmbedBuilderDsl {
    val embedBuilder = EmbedBuilder()

    var title: String? = null
    var description: String? = null
    var thumbnail: String? = null
    var image: String? = null
    var timestamp: TemporalAccessor? = null
    
    var color: Int = DEFAULT_COLOR
    
    fun color(color: Color) {
        this.color = color.rgb
    }

    data class AuthorBuilder(
        var name: String? = null,
        var url: String? = null,
        var iconUrl: String? = null
    )
    
    private var _author: AuthorBuilder? = null
    
    fun author(block: AuthorBuilder.() -> Unit) {
        _author = AuthorBuilder().apply(block)
    }
    
    data class FieldBuilder(
        var name: String? = null,
        var value: String? = null,
        var inline: Boolean = false
    )
    
    private val fieldBuilders = mutableListOf<FieldBuilder>()
    
    fun field(block: FieldBuilder.() -> Unit) {
        fieldBuilders += FieldBuilder().apply(block)
    }
    
    fun field(name: String?, value: String?, inline: Boolean = false) {
        fieldBuilders += FieldBuilder(name, value, inline)
    }
    
    fun blankField(inline: Boolean = false) {
        fieldBuilders += FieldBuilder("\u200e", "\u200e", inline)
    }
    
    data class FooterBuilder(
        var text: String? = null,
        var iconUrl: String? = null
    )
    
    private var _footer: FooterBuilder? = null
    
    fun footer(block: FooterBuilder.() -> Unit) {
        _footer = FooterBuilder().apply(block)
    }
    
    fun footer(text: String?, iconUrl: String? = null) {
        _footer = FooterBuilder(text, iconUrl)
    }
    
    fun build() = embedBuilder.apply {
        title?.let { setTitle(it) }
        description?.let { setDescription(it) }
        thumbnail?.let { setThumbnail(it) }
        image?.let { setImage(it) }
        timestamp?.let { setTimestamp(it) }
        setColor(color)
        
        _author?.let {
            setAuthor(it.name, it.url, it.iconUrl)
        }
        
        fieldBuilders.forEach {
            addField(it.name, it.value, it.inline)
        }
        
        _footer?.let {
            setFooter(it.text, it.iconUrl)
        }
    }.build()
    
    companion object {
        const val DEFAULT_COLOR = 536870911
    }
}