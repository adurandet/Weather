package com.adurandet.weather.repository

import com.adurandet.weather.model.CodeError

sealed class Resource<out T>
data class Success<out T>(val data: T) : Resource<T>()
data class Failure<out T>(val codeError: CodeError): Resource<T>()
data class Loading< out T>(val data: T? = null): Resource<T>()