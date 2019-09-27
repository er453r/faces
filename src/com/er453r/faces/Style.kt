package com.er453r.faces

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Style : Stylesheet() {
    companion object {
        val debug by cssclass()

        val green by cssclass()
        val red by cssclass()
        val bold by cssclass()
        val smaller by cssclass()
        val bigger by cssclass()
        val wider by cssclass()
        val underline by cssclass()
        val invalid by cssclass()
        val pending by cssclass()
    }

    init {
        root {
            prefHeight = 800.px
            prefWidth = 1600.px

            fontSize = 0.4.cm
        }

        label {
            and(red) {
                textFill = Color.RED
            }

            and(green) {
                textFill = Color.GREEN
            }
        }

        button {
            fontWeight = FontWeight.BOLD

            and(green) {
                backgroundColor += Color.GREEN
                textFill = Color.WHITE
            }

            and(red) {
                backgroundColor += Color.RED
                textFill = Color.WHITE
            }

            and(wider) {
                minWidth = 16.em
            }
        }

        debug {
            backgroundColor += c(255, 0, 0, 0.4)
        }

        bold {
            fontWeight = FontWeight.BOLD
        }

        smaller {
            fontSize = 0.8.em
        }

        bigger {
            fontSize = 1.2.em
        }

        underline {
            underline = true
        }

        invalid {
            borderColor += box(Color.RED)
        }

        pending {
            opacity = 0.5
        }

        tableCell {
            padding = box(0.5.em, 0.5.em, 0.2.em, 0.5.em)
            alignment = Pos.CENTER_LEFT
        }

        tableRowCell {
            odd {
                backgroundColor += c(0, 0, 0, 0.1)
            }
        }
    }
}
