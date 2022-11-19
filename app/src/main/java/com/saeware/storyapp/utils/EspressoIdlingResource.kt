package com.saeware.storyapp.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE_KEY = "GLOBAL"
    private val countingIdlingResource = CountingIdlingResource(RESOURCE_KEY)

    fun increment() { countingIdlingResource.increment() }

    fun decrement() { countingIdlingResource.decrement() }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment()
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement()
    }
}