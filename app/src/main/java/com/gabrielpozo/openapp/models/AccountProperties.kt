package com.gabrielpozo.openapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

@Entity(tableName = "account_properties")
data class AccountProperties(
    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var pk: Int,

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email")
    var email: String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username")
    var userName: String,

    @Expose
    @ColumnInfo(name = "timestamp")
    var timestamp: Int? = null
) {

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as AccountProperties

        if (pk != other.pk) return false
        if (email != other.email) return false
        if (userName != other.userName) return false
        //if (timestamp != other.timestamp) return false

        return true
    }
}