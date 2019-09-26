package com.er453r.faces

import com.er453r.faces.views.MainView
import javafx.stage.Stage
import tornadofx.App

class Application : App(MainView::class, Style::class) {
    override fun start(stage: Stage) {
        with(stage) {
            width = 400.0
            height = 300.0

            super.start(this)
        }
    }
}
