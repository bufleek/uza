package com.uza.data

data class PostItem(
    val title: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean = false,
    val images: List<PostItemImage>
)