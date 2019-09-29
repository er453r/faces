package com.er453r.faces.utils

import javafx.beans.value.ObservableValue
import javafx.util.Duration
import tornadofx.millis
import tornadofx.runLater
import java.util.*

fun <T> ObservableValue<T>.debounce(duration: Duration = 500.millis, debounceListener: (T) -> Unit) {
    var timerTask: TimerTask? = null

    this.addListener { _: ObservableValue<out T>?, _: T, newValue: T ->
        timerTask?.cancel()

        timerTask = runLater(duration) {
            debounceListener(newValue)
        }
    }
}
