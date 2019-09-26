package com.er453r.faces.views

import mu.KotlinLogging
import tornadofx.*

class MainView : View() {
    private val logger = KotlinLogging.logger {}

    override val root = borderpane {
        top {
            label("top")
        }

        center {
            label("Center")
        }

        bottom {
            label("Bottom")
        }
    }

    init {
        logger.info("Init!")
    }

    override fun onDock() {
        logger.info { "Application start!" }
    }

    override fun onUndock() {
        logger.info { "Application exit, clean up..." }
    }
}
