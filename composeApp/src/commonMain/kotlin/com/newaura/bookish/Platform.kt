package com.newaura.bookish

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform