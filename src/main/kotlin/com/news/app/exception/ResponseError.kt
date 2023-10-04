package com.news.app.exception

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ResponseError(val code: Int, val message: String) {

    ALREADY_REGISTERED(1000, "Already registered!"),

    BAD_CREDENTIALS(1001, "Bad credentials!"),

    BAD_REQUEST(400, "Bad request!!"),

    NOT_FOUND(404, "Resource not found!"),

    INTERNAL_SERVER_ERROR(500, "Internal server error!")
}

