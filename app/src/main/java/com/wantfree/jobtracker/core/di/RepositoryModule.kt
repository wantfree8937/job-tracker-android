package com.wantfree.jobtracker.core.di

import com.wantfree.jobtracker.data.repository.AiRepositoryImpl
import com.wantfree.jobtracker.data.repository.AuthRepositoryImpl
import com.wantfree.jobtracker.data.repository.JobRepositoryImpl
import com.wantfree.jobtracker.domain.repository.AiRepository
import com.wantfree.jobtracker.domain.repository.AuthRepository
import com.wantfree.jobtracker.domain.repository.JobRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 인터페이스 → 구현체 바인딩 (클린 아키텍처 의존성 역전) */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindJobRepository(impl: JobRepositoryImpl): JobRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository
}
