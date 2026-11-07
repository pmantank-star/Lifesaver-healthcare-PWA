package com.example.credential

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CredentialEntity::class], version = 1, exportSchema = false)
abstract class LifeSaversDatabase : RoomDatabase() {

    abstract fun credentialDao(): CredentialDao

    companion object {
        @Volatile
        private var INSTANCE: LifeSaversDatabase? = null

        fun getInstance(context: Context): LifeSaversDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeSaversDatabase::class.java,
                    "lifesavers_healthcare.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

typealias OmaDatabase = LifeSaversDatabase
