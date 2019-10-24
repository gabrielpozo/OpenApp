package com.gabrielpozo.openapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrielpozo.openapp.models.AccountProperties

@Dao
interface AccountPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk: Int): LiveData<AccountProperties>

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchTimeStampByPk(pk: Int): AccountProperties

    @Query("SELECT * FROM account_properties WHERE email = :email")
    fun searchByEmail(email: String): AccountProperties?

    @Query("Update account_properties SET email = :email, username = :username, timestamp =:timestamp WHERE pk = :pk")
    fun updateAccountProperties(pk: Int, email: String, username: String, timestamp: Int)

    @Query("Update account_properties SET timestamp =:timestamp WHERE pk = :pk")
    fun updateAccountTimestampProperty(pk: Int, timestamp: Int)

}