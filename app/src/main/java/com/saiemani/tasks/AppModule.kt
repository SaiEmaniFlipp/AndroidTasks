package com.saiemani.tasks

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalTasksDataSource

    @Singleton
    @LocalTasksDataSource
    @Provides
    fun provideTasksLocalDataSource(
        database: TasksDatabase,
        ioDispatcher: CoroutineDispatcher
    ): ITasksDataSource {
        return TasksLocalDataSource(
            database.taskDao(), ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): TasksDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TasksDatabase::class.java,
            "Tasks.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

/**
 * The binding for TasksRepository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object TasksRepositoryModule {

    @Singleton
    @Provides
    fun provideTasksRepository(
        @AppModule.LocalTasksDataSource localTasksDataSource: ITasksDataSource,
        ioDispatcher: CoroutineDispatcher
    ): ITasksRepository {
        return TasksRepository(
            localTasksDataSource,
            ioDispatcher
        )
    }
}
