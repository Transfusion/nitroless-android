package io.github.transfusion.nitroless.storage

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// TODO: perhaps override .equals()
@Entity
data class NitrolessRepo(
    @PrimaryKey
    @NonNull
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "url") val url: String
)