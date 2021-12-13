package iamzen.`in`.timework

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
class Task(val Name:String,val Description:String?,val ShortOrder:Int?): Parcelable {
    var id:Long = 0
}