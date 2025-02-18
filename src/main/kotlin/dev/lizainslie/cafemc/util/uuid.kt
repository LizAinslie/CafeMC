package dev.lizainslie.cafemc.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

val uuidPattern = Regex("([0-9A-Fa-f]{8})([0-9A-Fa-f]{4})([0-9A-Fa-f]{4})([0-9A-Fa-f]{4})([0-9A-Fa-f]{12})")

fun convertUuidWithoutDashesToUuidWithDashes(inputUuid: String) = uuidPattern.replace(inputUuid) { matchResult ->
    matchResult.groupValues.subList(1, 6).joinToString("-")
}

fun isUuid(input: String) = try {
    UUID.fromString(input)
    true
} catch (e: IllegalArgumentException) {
    false
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}
