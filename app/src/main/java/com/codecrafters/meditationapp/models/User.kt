package com.codecrafters.meditationapp.models

import android.os.Parcel
import android.os.Parcelable

class User(val uid: String, val username: String, val profileImageUrl: String) : Parcelable {
  constructor() : this("", "", "")

  // Implementing the describeContents() method of the Parcelable interface
  override fun describeContents(): Int {
    return 0
  }

  // Implementing the writeToParcel() method of the Parcelable interface
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(uid)
    dest.writeString(username)
    dest.writeString(profileImageUrl)
  }

  // Companion object that implements the Parcelable.Creator interface
  companion object CREATOR : Parcelable.Creator<User> {
    override fun createFromParcel(parcel: Parcel): User {
      return User(parcel)
    }

    override fun newArray(size: Int): Array<User?> {
      return arrayOfNulls(size)
    }
  }

  // Secondary constructor that reads from Parcel to reconstruct the object
  private constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: ""
  )
}
