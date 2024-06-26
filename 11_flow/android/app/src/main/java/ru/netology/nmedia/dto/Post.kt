package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    var authorId: Long,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val hidden: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
