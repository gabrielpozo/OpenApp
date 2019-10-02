package com.gabrielpozo.openapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account_properties")
data class AccountProperties(
    @SerializedName("pk")
    @Expose
    @PrimaryKey
    @ColumnInfo
    var pk: Int,

    @SerializedName("pk")
    @Expose
    @ColumnInfo
    var email: String,

    @SerializedName("pk")
    @Expose
    @ColumnInfo
    var userName: String
) {
}