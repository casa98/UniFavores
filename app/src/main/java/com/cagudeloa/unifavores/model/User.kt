package com.cagudeloa.unifavores.model

data class User(
    var uid: String = "",
    var username: String = "",
    var image: String = "",
    // This is the starting score value
    var score: Int = 2
)