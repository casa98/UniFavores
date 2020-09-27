package com.cagudeloa.unifavores.model

data class Favor(
    var id: String = "",
    var user: String = "Anonymous user",
    var favorTitle: String = "Favor title",
    var favorDescription: String = "Favor description",
    var completed: Boolean = false
)