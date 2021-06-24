package io.github.transfusion.nitroless.ui.home.bottomsheet

data class CreditsDataItem(
    val id: Int,
    val name: String,
    val roles: Collection<String>,
    val github_username: String?,
    val twitter_username: String?
)