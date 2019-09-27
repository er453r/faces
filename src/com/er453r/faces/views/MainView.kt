package com.er453r.faces.views

import com.er453r.faces.components.ImageCell
import javafx.beans.property.SimpleStringProperty
import javafx.stage.DirectoryChooser
import mu.KotlinLogging
import org.controlsfx.control.GridView
import tornadofx.*
import java.io.File

class MainView : View() {
    private val logger = KotlinLogging.logger {}

    private val directoryProperty = SimpleStringProperty("/home/mkotz/Downloads/debki")
    private val filesProperty = mutableListOf<File>().asObservable()

    override val root = borderpane {
        top {
            label("top")
        }

        center {
            vbox {
                label("Center")

                hbox {
                    textfield(directoryProperty) {

                    }

                    button("Change") {
                        action {
                            val directoryChooser = DirectoryChooser()

                            directoryChooser.title = "Dir with images..."
                            directoryChooser.initialDirectory = File(System.getProperty("user.home"))

                            directoryChooser.showDialog(primaryStage)?.let { directoryProperty.set(it.absolutePath) }
                        }
                    }

                    button("Scan") {
                        enableWhen(directoryProperty.isNotEmpty)

                        action {
                            val files = File(directoryProperty.value).walkTopDown().filter { it.absolutePath.endsWith(".jpg") }

                            filesProperty.clear()
                            filesProperty.addAll(files)

                            logger.info { "Found ${files.count()} files!" }
                        }
                    }
                }

                add(GridView(filesProperty).apply {
                    setCellFactory {
                        ImageCell()
                    }

                    cellHeight = 256.0
                    cellWidth = 256.0
                    prefHeight = 1200.0
                })
            }
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
