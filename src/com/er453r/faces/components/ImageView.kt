package com.er453r.faces.components

import com.er453r.faces.utils.image
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.layout.Region
import tornadofx.*
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ImageView(file: File? = null) : Region() {
    companion object {
        private val executor = Executors.newFixedThreadPool(2)
    }

    private val imageProperty = SimpleObjectProperty<Image>()
    private val loadingText = SimpleStringProperty()
    private val imageOpacity = SimpleDoubleProperty(0.0)
    private var job:Future<*>? = null
    private var file:File? = null

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
            }
        )

        file?.let {
            update(it)
        }
    }

    fun update(file: File?) {
        this.file = file

        loadingText.value = "Loading ${file?.absolutePath ?: "derp"}"
        imageOpacity.value = 0.2

        file?.let {
            job?.let {
                if(!(it.isDone || it.isCancelled))
                    it.cancel(true)
            }

            job = executor.submit {
                val path = it.absolutePath
                val image = it.image(256)
                
                runLater {
                    if(this.file?.absolutePath == path){ // maybe file changed
                        imageProperty.value = image
                        imageOpacity.value = 1.0
                    }
                }
            }
        }
    }
}
