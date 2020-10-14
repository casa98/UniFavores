package com.cagudeloa.unifavores.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favor(
    var user: String = "",
    var username: String = "",
    var assignedUser: String = "",
    var assignedUsername: String = "",
    var favorTitle: String = "",
    var favorDescription: String = "",
    var favorLocation: String = "",
    val creationDate: String = "",
    val key: String = "",
    var status: String = "0"
    /**
     *  0 --> not-assigned
     * -1 --> assigned
     *  1 --> completed
     */
) : Parcelable