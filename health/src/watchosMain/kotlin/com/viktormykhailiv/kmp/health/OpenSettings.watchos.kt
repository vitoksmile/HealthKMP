package com.viktormykhailiv.kmp.health

internal actual fun openAppleHealthSettings(): Result<Unit> =
    Result.failure(NotImplementedError("Opening health settings is not supported on watchOS"))
