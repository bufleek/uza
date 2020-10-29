package com.uza.data.models

data class PostItem(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var price: String = "",
    var isAvailable: Boolean = false,
    var dateCreated: Long? = null,
    var lastModified: Long? = null,
    var images: ArrayList<String> = ArrayList(),
    var sellerId: String? = null
)