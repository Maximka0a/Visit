package com.example.visit.di

import android.content.Context
import androidx.room.Room
import com.example.visit.data.local.ContactDataBase
import com.example.visit.data.local.dao.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun providesDataBase(@ApplicationContext context: Context): ContactDataBase {
        return Room.databaseBuilder(
            context = context,
            klass = ContactDataBase::class.java,
            name = "ContactDataBase"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun getDao(contactDataBase: ContactDataBase): ContactDao{
        return contactDataBase.contactDao()
    }
}