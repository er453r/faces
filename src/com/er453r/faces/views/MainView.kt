package com.er453r.faces.views

import javafx.beans.property.SimpleStringProperty
import javafx.stage.DirectoryChooser
import mu.KotlinLogging
import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_imgproc.equalizeHist
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.RectVector
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier
import org.opencv.imgproc.Imgproc
import tornadofx.*
import java.io.File


class MainView : View() {
    private val logger = KotlinLogging.logger {}

    private val directoryProperty = SimpleStringProperty("/home/mkotz/Downloads/debki")

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

                            files.forEach {
                                findFaces(it)
                            }

                            logger.info { "Found ${files.count()} files!" }
                        }
                    }
                }
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

    fun findFaces(file: File) {
        // Load Face Detector
        val faceDetector = CascadeClassifier("/home/mkotz/IdeaProjects/family/faces/res/haarcascade_frontalface_alt2.xml")

        // Load image
        val img = imread(file.absolutePath)

        // convert to grayscale and equalize histograe for better detection
        val gray = Mat()
        cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)
        equalizeHist(gray, gray)

        // Find faces on the image
        val faces = RectVector()

        faceDetector.detectMultiScale(gray, faces)

        logger.info { "File ${file.absolutePath}, Faces detected: " + faces.size() }
    }
}
