package com.example.animalhealth.utilities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Clinic(
    var id:String?=null,
    var name:String?=null,
    var adress:String?=null,
    var photo:String?=null,
    var rate:Float?=null,
    var fecha:String?=null,
    var not_state:Int?=null,
    var userNotifications:String?=null
): Parcelable