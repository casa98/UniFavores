package com.cagudeloa.unifavores.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favor(
    var user: String = "Anonymous user",
    var assignedUser: String = "",
    var favorTitle: String = "Favor title",
    var favorDescription: String = "Favor description",
    val creationDate: String = "",
    var status: String = "0"
    /**
     *  0 --> not-assigned
     * -1 --> assigned
     *  1 --> completed
     */
) : Parcelable