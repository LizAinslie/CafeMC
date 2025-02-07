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
    private val embedBuilder = EmbedBuilder()

    var title: String? = null
    var description: String? = null
    var url: String? = null
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
    
    fun author(name: String?, url: String? = null, iconUrl: String? = null) {
        _author = AuthorBuilder(name, url, iconUrl)
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
        title?.let { setTitle(it, url) } // todo: maybe set the url differently?
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

@Suppress("unused")
internal fun dslExample() {
    val messageEmbed = DiscordUtils.buildEmbed {
        // some basic embed properties
        title = "Title"
        url = "https://example.com"
        description = "Description"
        thumbnail = "https://example.com/thumbnail.png"
        image = "https://example.com/image.png"
        timestamp = java.time.Instant.now()
        
        
        // to set the color...
        color = 0xFFFFFF // you can use an int
        color(Color.RED) // or a java.awt.Color
        
        
        author {// you can add an author with a block
            name = "Author"
            url = "https://example.com/author"
            iconUrl = "https://example.com/icon.png"
        }
        
        // or a function
        author("Author", "https://example.com/author", "https://example.com/icon.png")
        
        
        // fields can be added in a few ways
        
        field { // you can use a block
            name = "Block Field 1"
            value = "Block Value 1"
            // inline is false by default
            inline = true
        }
        
        // or you can use a function
        field("Field 1", "Value 1")
        field("Field 2", "Value 2", inline = true)
        
        // you can also add blank fields
        // this is a bit of a hack but useful for formatting
        blankField()
        
        
        // you can also add a footer with a function
        footer("Footer", "https://example.com/footer.png")
        
        footer { // or a block
            text = "Footer"
            iconUrl = "https://example.com/footer.png"
        }
    }
}