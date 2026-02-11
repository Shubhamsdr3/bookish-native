package com.newaura.bookish.core.data

sealed interface ButtonState {
    data object Loading: ButtonState
    data object Disabled: ButtonState
    data object Enabled: ButtonState
}