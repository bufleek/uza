package com.uza.models

data class Post(
    val sellerId: String = "",
    val available: Boolean = false,
    val dateCreated: Long = 0,
    val description: String = "",
    val images: List<String> = listOf(),
    val price: String = "",
    val title: String = ""
)