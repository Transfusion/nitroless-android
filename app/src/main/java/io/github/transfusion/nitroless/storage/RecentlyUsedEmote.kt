package io.github.transfusion.nitroless.storage

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = NitrolessRepo::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("repoId"),
        onDelete = ForeignKey.CASCADE
    )],

    indices = [Index(
        value = ["repoId", "emote_path",
            "emote_name", "emote_type"], unique = true
    )]
)
data class RecentlyUsedEmote(
    @PrimaryKey
    @NonNull
    val emote_id: UUID = UUID.randomUUID(),

    @NonNull
    val repoId: UUID,

    val emote_path: String,
    val emote_name: String,
    val emote_type: String,
    val emote_used: Date,
)

data class RecentlyUsedEmoteAndRepo(
    @Embedded
    val recentlyUsedEmote: RecentlyUsedEmote,

    @Relation(
        parentColumn = "repoId",
        entityColumn = "id", entity = NitrolessRepo::class
    )

    val repo: NitrolessRepo

)