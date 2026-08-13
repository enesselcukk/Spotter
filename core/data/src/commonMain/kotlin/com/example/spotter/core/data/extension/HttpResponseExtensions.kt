package com.example.spotter.core.data.extension

import com.example.spotter.core.data.exception.HttpStatusException
import com.example.spotter.core.domain.result.RestResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T : Any> HttpResponse.asRestResult(): RestResult<T> =
    if (status.isSuccess()) {
        RestResult.Success(body())
    } else {
        RestResult.Error(
            HttpStatusException(
                statusCode = status.value,
                message = status.description,
            ),
        )
    }
