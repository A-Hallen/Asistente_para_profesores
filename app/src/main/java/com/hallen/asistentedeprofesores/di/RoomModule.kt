package com.hallen.asistentedeprofesores.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.hallen.asistentedeprofesores.data.database.DataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val DATABASE_NAME = "database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(context, DataBase::class.java,
        DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideAssistanceDao(db: DataBase) = db.getAssistanceDao()

    @Singleton
    @Provides
    fun provideClaseDao(db: DataBase) = db.getClaseDao()

    @Singleton
    @Provides
    fun provideEventDao(db: DataBase) = db.getEventDao()

    @Singleton
    @Provides
    fun provideGroupDao(db: DataBase) = db.getGroupDao()

    @Singleton
    @Provides
    fun provideNotaDao(db: DataBase) = db.getNotaDao()

    @Singleton
    @Provides
    fun provideStudentDao(db: DataBase) = db.getStudentDao()

    @Singleton
    @Provides
    fun provideChatDao(db: DataBase) = db.getChatDao()

}