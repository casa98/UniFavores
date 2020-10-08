package com.cagudeloa.unifavores.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var uid: String = "",
    var username: String = "",
    var image: String = "",
    var message: String = "None"
): Parcelable