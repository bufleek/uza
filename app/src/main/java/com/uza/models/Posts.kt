package com.uza.models

data class Posts(val posts: List<Post> = listOf())

data class Post(
    val available: Boolean = false,
    val dateCreated: Int = 0,
    val description: String = "",
    val images: List<String> = listOf(),
    val price: String = "",
    val title: String = ""
)