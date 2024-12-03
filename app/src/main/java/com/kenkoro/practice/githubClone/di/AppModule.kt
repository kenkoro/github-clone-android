package com.kenkoro.practice.githubClone.di

import com.kenkoro.practice.githubClone.reposViewer.data.networking.GithubApi
import com.kenkoro.projects.githubClone.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
  private val contentType = MediaType.get("application/vnd.github+json")
  private val okHttpClient =
    OkHttpClient.Builder()
      .addInterceptor { chain ->
        val modifiedRequest =
          chain.request().newBuilder()
            .header("X-GitHub-Api-Version", "2022-11-28")
            .build()
        chain.proceed(modifiedRequest)
      }
      .build()
  private val retrofit =
    Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(
        Json.asConverterFactory(
          contentType = contentType,
        ),
      )
      .build()

  @Provides
  @Singleton
  fun provideGithubApi(): GithubApi {
    return retrofit.create<GithubApi>()
  }
}