package iamzen.`in`.timework

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(val Name:String,val Description:String?,val ShortOrder:Int?,var id:Long = 0): Parcelable