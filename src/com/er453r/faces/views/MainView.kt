package com.er453r.faces.views

import com.er453r.faces.components.ImageCell
import com.er453r.faces.components.ImageFragment
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Rectangle2D
import javafx.scene.control.SelectionMode
import javafx.stage.DirectoryChooser
import mu.KotlinLogging
import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_imgproc.equalizeHist
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.RectVector
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier
import org.controlsfx.control.GridView
import org.opencv.imgproc.Imgproc
import tornadofx.*
import java.io.File


class MainView : View() {
    private val logger = KotlinLogging.logger {}

    private val directoryProperty = SimpleStringProperty("/home/mkotz/Downloads/test")
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
                            val files =
                                File(directoryProperty.value).walkTopDown().filter { it.absolutePath.endsWith(".jpg") }

                            filesProperty.clear()
                            filesProperty.addAll(files)

//                            files.forEach { file ->
//                                val thumbnail = thumb(file.absolutePath)
//
//                                imageProperty.value = thumbnail
//
//                                val faces = findFaces(file, true)
//
//                                overlay.children.clear()
//
//                                faces.forEach {
//                                    overlay.add(
//                                        rectangle(it.minX * thumbnail.width, it.minY * thumbnail.height, it.width * thumbnail.width, it.height * thumbnail.height) {
//                                            stroke = Color.RED
//                                            strokeWidth = 4.0
//                                            fill = null
//
//                                            setOnMouseEntered {
//                                                this.fill = Color.RED
//                                            }
//
//                                            setOnMouseExited {
//                                                this.fill = null
//                                            }
//                                        }
//                                    )
//                                }
//                            }

                            logger.info { "Found ${files.count()} files!" }
                        }
                    }
                }

//                add(GridView(filesProperty).apply {
//                    setCellFactory {
//                        ImageCell()
//                    }
//
//                    cellHeight = 256.0
//                    cellWidth = 256.0
//                    prefHeight = 400.0
//                })

                listview(filesProperty) {
                    cellFragment(ImageFragment::class)

                    placeholder = label("Select directory") {
                        opacity = 0.6
                    }

                    selectionModel.selectionMode = SelectionMode.SINGLE

                    onUserSelect {
                        logger.info { "Selected!" }
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

    fun findFaces(file: File, relative: Boolean = false): Set<Rectangle2D> {
        // Load Face Detector
        val faceDetector = CascadeClassifier("/home/mkotz/IdeaProjects/ktex/faces/res/haarcascade_frontalface_alt2.xml")

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

        val facesSet = mutableSetOf<Rectangle2D>()

        for (n in 0 until faces.size()) {
            val face = faces.get(n)

            if (relative)
                facesSet.add(
                    Rectangle2D(
                        face.x().toDouble() / gray.arrayWidth(),
                        face.y().toDouble() / gray.arrayHeight(),
                        face.width().toDouble() / gray.arrayWidth(),
                        face.height().toDouble() / gray.arrayHeight()
                    )
                )
            else
                facesSet.add(
                    Rectangle2D(
                        face.x().toDouble(),
                        face.y().toDouble(),
                        face.width().toDouble(),
                        face.height().toDouble()
                    )
                )
        }

        return facesSet
    }


}
