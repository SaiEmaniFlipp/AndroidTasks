package com.saiemani.tasks.di

import com.saiemani.tasks.data.FakeRepository
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * TasksRepository binding to use in tests.
 *
 * Hilt will inject a [FakeRepository] instead of a [TasksRepository].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TestTasksRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRepository(repo: FakeRepository): ITasksRepository
}
