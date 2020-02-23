package com.adurandet.weather.model

sealed class CodeError(val message: String = "")
class CallError(message: String) : CodeError(message)
class BadRequestError : CodeError()
class DataNotFoundError : CodeError()