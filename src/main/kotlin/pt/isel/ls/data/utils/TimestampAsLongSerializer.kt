package pt.isel.ls.data.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.sql.Timestamp

/*
    java.sql.Timestamp can be used for Postgres Timestamp, but cannot be serialized with Kotlinx oob.
    Thankfully this seems to be a way around it.
    https://github.com/Kotlin/kotlinx.serialization/issues/23#issuecomment-1141089814
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#specifying-serializer-on-a-property
    https://github.com/Kotlin/kotlinx.serialization/blob/master/guide/example/example-serializer-15.kt
 */
object TimestampAsLongSerializer : KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Timestamp) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Timestamp = Timestamp(decoder.decodeLong())
}
