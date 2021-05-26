package io.github.transfusion.nitroless.data

data class NitrolessRepoModel(
    val name: String,
    val path: String,
    val emotes: List<NitrolessRepoEmoteModel>
)