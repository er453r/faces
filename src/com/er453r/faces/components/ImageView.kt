package com.er453r.faces.components

import com.er453r.faces.utils.FaceDetector
import com.er453r.faces.utils.debounce
import com.er453r.faces.utils.image
import com.er453r.faces.utils.toMat
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ImageView(file: File? = null) : Region() {
    companion object {
        private val executor = Executors.newFixedThreadPool(8)
    }

    private val fileProperty = SimpleObjectProperty<File>()
    private val imageProperty = SimpleObjectProperty<Image>()
    private val loadingText = SimpleStringProperty()
    private val imageOpacity = SimpleDoubleProperty(0.0)

    private var job: Future<*>? = null

    private val faceDetector = FaceDetector()
    private val overlay = group { }

    init {
        val region = this

        add(
            stackpane {
                label(loadingText) {
                    visibleWhen(imageProperty.isNull)
                    managedWhen(imageProperty.isNull)
                }

                imageview(imageProperty) {
                    opacityProperty().bindBidirectional(imageOpacity)

                    isPreserveRatio = true

                    fitHeightProperty().bind(region.heightProperty())
                    fitWidthProperty().bind(region.widthProperty())
                }

                add(overlay)
            }
        )

        imageProperty.debounce { image ->
            val faces = faceDetector.findFaces(image.toMat(), true)

            overlay.children.clear()

            faces.forEach { rectangle ->
                overlay.add(
                    rectangle(rectangle.minX * image.width, rectangle.minY * image.height, rectangle.width * image.width, rectangle.height * image.height) {
                        stroke = Color.RED
                        strokeWidth = 4.0
                        fill = null

                        setOnMouseEntered {
                            this.fill = Color.RED
                        }

                        setOnMouseExited {
                            this.fill = null
                        }
                    }
                )
            }

            val margin = image.width * 0.2

            overlay.add(
                rectangle(margin, margin, image.width -2*margin, image.height -2*margin) {
                    stroke = Color.BLUE
                    strokeWidth = 4.0
                    fill = null
                }
            )
        }

        fileProperty.debounce {
            overlay.children.clear()

            loadingText.value = "Loading ${it?.absolutePath ?: "empty"}"
            imageOpacity.value = 0.2

            it?.let { file ->
                job?.let { job ->
                    if (!(job.isDone || job.isCancelled))
                        job.cancel(true)
                }

                job = executor.submit {
                    val path = file.absolutePath
                    val image = file.image(512)

                    runLater {
                        if (fileProperty.value.absolutePath == path) { // maybe file changed
                            imageProperty.value = image
                            imageOpacity.value = 1.0
                        }
                    }
                }
            }
        }

        update(file)
    }

    fun update(file: File?) {
        fileProperty.value = file
    }
}
