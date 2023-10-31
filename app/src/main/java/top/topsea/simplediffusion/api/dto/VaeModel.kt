package top.topsea.simplediffusion.api.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class VaeModel(
    val model_name: String,
    val filename: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(model_name)
        parcel.writeString(filename)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VaeModel> {
        override fun createFromParcel(parcel: Parcel): VaeModel {
            return VaeModel(parcel)
        }

        override fun newArray(size: Int): Array<VaeModel?> {
            return arrayOfNulls(size)
        }
    }
}

