package dev.lizainslie.cafemc.chat

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import kotlin.math.floor


/**
 * DSL for creating tables.
 * 
 * This is an incredibly cursed hack that uses hardcoded character widths based on the default Minecraft font.
 * It will not work with custom fonts!
 * 
 * Most of this is shamelessly copied from https://github.com/FisheyLP/TableGenerator/blob/master/TableGenerator.java
 * It has been converted to Kotlin and adapted to work with Adventure components.
 *
 * @param alignments The alignments of the columns in the table.
 */
class TableDsl(private val alignments: List<Alignment>, private val borderColor: TextColor = NamedTextColor.GRAY) {
    private val columns = alignments.size
    private val rows = mutableListOf<RowDsl>()
    
    init {
        require(columns > 0) { "Table must have at least one column" }
    }
    
    fun row(block: RowDsl.() -> Unit) {
        rows += RowDsl(columns).apply(block).also { it.finalizeRow() }
    }
    
    fun build(receiver: Receiver): Component {
        val columnWidths = IntArray(columns) { 0 }
        
        rows.forEach { row ->
            row.cells.forEachIndexed { i, cell ->
                columnWidths[i] = maxOf(columnWidths[i], getCustomLength(cell.toPlainText(), receiver))
            }
        }
        
        val lines = mutableListOf<Component>()
        
        rows.forEach { row ->
            var line = Component.empty()
            
            for (i in 0 until columns) {
                val align = alignments[i]
                val cell = row.cells[i]
                val length = getCustomLength(cell.toPlainText(), receiver)
                
                val padding = columnWidths[i] - length
                var spacesAmount = padding
                if (receiver == Receiver.PLAYER) spacesAmount = floor(padding / 4.0).toInt()
                var char1Amount = 0
                if (receiver == Receiver.PLAYER) char1Amount = padding - 4 * spacesAmount
                
                val spaces = concatChars(' ', spacesAmount)
                val char1s = concatChars(char1, char1Amount)
                
                
                
                when (align) {
                    Alignment.LEFT -> {
                        var c = cell
                        if (i < columns - 1) 
                            c = c.append(Component.text(char1s + spaces).color(NamedTextColor.DARK_GRAY))
                        line = line.append(c)
                    }
                    Alignment.CENTER -> {
                        val leftAmount = padding / 2
                        val rightAmount = padding - leftAmount
                        
                        var spacesLeftAmount = leftAmount
                        var spacesRightAmount = rightAmount
                        
                        if (receiver == Receiver.PLAYER) {
                            spacesLeftAmount = floor(spacesLeftAmount / 4.0).toInt()
                            spacesRightAmount = floor(spacesRightAmount / 4.0).toInt()
                        }
                        
                        var char1LeftAmount = 0
                        var char1RightAmount = 0
                        
                        if (receiver == Receiver.PLAYER) {
                            char1LeftAmount = leftAmount - 4 * spacesLeftAmount
                            char1RightAmount = rightAmount - 4 * spacesRightAmount
                        }
                        
                        val spacesLeft = concatChars(' ', spacesLeftAmount)
                        val spacesRight = concatChars(' ', spacesRightAmount)
                        val char1Left = concatChars(char1, char1LeftAmount)
                        val char1Right = concatChars(char1, char1RightAmount)
                        
                        var c = Component
                            .text(spacesLeft + char1Left).color(NamedTextColor.DARK_GRAY)
                            .append(cell)
                        
                        if (i < columns - 1) 
                            c = c.append(Component.text(char1Right + spacesRight).color(NamedTextColor.DARK_GRAY))
                        
                        line = line.append(c)
                    }
                    Alignment.RIGHT -> {
                        val c = Component.text(spaces + char1s).color(NamedTextColor.DARK_GRAY)
                            .append(cell)
                        line = line.append(c)
                    }
                }
                
                if (i < columns - 1) 
                    line = line.append(Component.text(DELIMITER).color(borderColor))
            }
            
            lines += line
        }
        
        return lines.joinToComponent(Component.newline())
    }

    private fun concatChars(c: Char, length: Int): String {
        var s = ""
        if (length < 1) return s

        for (i in 0 until length) s += c.toString()
        return s
    }


    class RowDsl(private val columns: Int) {
        val cells = mutableListOf<Component>()
        
        fun cell(block: ComponentDsl.() -> Unit) {
            // prevent adding too many cells
            if (cells.size == columns) 
                throw IllegalStateException("Too many cells for the table. Max: $columns")
            
            cells += component(block)
        }
        
        fun finalizeRow() {
            // fill empty cells with empty components
            // this screams NullPointerException, but hopefully it works
            // [famous last words]
            for (i in 0 until columns)
                if (i >= cells.size)
                    cells += Component.empty()
        }
    }
    
    enum class Alignment {
        LEFT, CENTER, RIGHT
    }

    enum class Receiver {
        PLAYER, CONSOLE;
        
        companion object {
            fun forAudience(audience: Audience) = when (audience) {
                is Player -> PLAYER
                is ConsoleCommandSender -> CONSOLE
                else -> throw IllegalArgumentException("Unsupported audience type: $audience")
            }
        }
    }

    companion object {
        const val DELIMITER = " | "

        // May the gods have mercy on my soul for what I am about to do, if they even exist.
        // This code might have killed them.

        // Character widths for the default Minecraft font
        private val char7 = listOf('°', '~', '@')
        private val char5 = listOf('"', '{', '}', '(', ')', '*', 'f', 'k', '<', '>')
        private val char4 = listOf('I', 't', ' ', '[', ']', '€')
        private val char3 = listOf('l', '`', '³', '\'')
        private val char2 = listOf(',', '.', '!', 'i', '´', ':', ';', '|')
        private const val char1 = '\u17f2'

        fun getCustomLength(text: String, receiver: Receiver): Int {
            if (receiver === Receiver.CONSOLE) return text.length

            var length = 0
            for (c in text.toCharArray()) length += getCustomCharLength(c)

            return length
        }

        fun getCustomCharLength(c: Char): Int {
            if (char1 == c) return 1
            if (char2.contains(c)) return 2
            if (char3.contains(c)) return 3
            if (char4.contains(c)) return 4
            if (char5.contains(c)) return 5
            if (char7.contains(c)) return 7

            return 6
        }
    }
}

fun table(
    alignments: List<TableDsl.Alignment>,
    borderColor: TextColor = NamedTextColor.GRAY,
    receiver: TableDsl.Receiver = TableDsl.Receiver.PLAYER,
    block: TableDsl.() -> Unit
) = TableDsl(alignments, borderColor).apply(block).build(receiver)


