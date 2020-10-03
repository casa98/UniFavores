package com.cagudeloa.unifavores.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favor(
    var user: String = "Anonymous user",
    var favorTitle: String = "Favor title",
    var favorDescription: String = "Favor description",
    var completed: String = "false"
) : Parcelable