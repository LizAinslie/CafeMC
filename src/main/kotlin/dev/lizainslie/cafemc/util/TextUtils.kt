package dev.lizainslie.cafemc.util

import java.util.*

object TextUtils {
    fun capitalizeEnumConstant(name: String): String {
        return name.split("_").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString() 
            }
        }
    }
}