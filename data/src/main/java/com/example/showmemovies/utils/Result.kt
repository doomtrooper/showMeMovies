package com.example.showmemovies.utils

sealed interface Result<out S, out E>{
    data class Success<S>(val body: S): Result<S, Nothing>
    data class Error<E>(val body: E): Result<Nothing, E>
}
