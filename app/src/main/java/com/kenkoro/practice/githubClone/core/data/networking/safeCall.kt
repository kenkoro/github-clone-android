package com.kenkoro.practice.githubClone.core.data.networking

import com.kenkoro.practice.githubClone.core.domain.util.NetworkError
import com.kenkoro.practice.githubClone.core.domain.util.Result
import kotlinx.coroutines.ensureActive
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(execute: () -> Response<T>): Result<T, NetworkError> {
  val response =
    try {
      execute()
    } catch (e: IOException) {
      return Result.Error(NetworkError.NoInternet)
    } catch (e: Exception) {
      /*
       * Throws a specific exception if there's an error and the
       * coroutine scope is no longer active.
       */
      coroutineContext.ensureActive()
      return Result.Error(NetworkError.Unknown)
    }

  return responseToResult(response)
}