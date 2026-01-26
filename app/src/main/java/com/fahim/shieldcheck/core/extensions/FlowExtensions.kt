package com.fahim.shieldcheck.core.extensions

import com.fahim.shieldcheck.core.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it, it.message)) }
}

fun <T> Flow<Result<T>>.onSuccess(action: suspend (T) -> Unit): Flow<Result<T>> {
    return map { result ->
        if (result is Result.Success) {
            action(result.data)
        }
        result
    }
}

fun <T> Flow<Result<T>>.onError(action: suspend (Throwable, String?) -> Unit): Flow<Result<T>> {
    return map { result ->
        if (result is Result.Error) {
            action(result.exception, result.message)
        }
        result
    }
}
