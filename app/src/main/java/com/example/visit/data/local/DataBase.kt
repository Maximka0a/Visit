package com.example.visit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.visit.data.local.dao.ContactDao
import com.example.visit.data.local.entity.ScannedContactEntity
import kotlin.jvm.java

@TypeConverters(Converters:: class)
@Database(entities = [(ScannedContactEntity::class)], version = 1)
abstract class ContactDataBase: RoomDatabase() {
    abstract fun contactDao(): ContactDao
}