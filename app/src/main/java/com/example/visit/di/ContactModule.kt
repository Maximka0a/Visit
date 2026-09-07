package com.example.visit.di

import com.example.visit.data.repository.ContactsRepositoryImpl
import com.example.visit.domain.repository.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactModule {
    @Binds
    abstract fun bindContactRepository(impl: ContactsRepositoryImpl):
            ContactsRepository
}