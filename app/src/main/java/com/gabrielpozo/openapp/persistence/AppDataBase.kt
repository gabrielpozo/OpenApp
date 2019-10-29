package com.gabrielpozo.openapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gabrielpozo.openapp.models.AccountProperties
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.models.BlogPost

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}