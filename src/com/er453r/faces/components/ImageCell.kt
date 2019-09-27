package com.er453r.faces.components

import org.controlsfx.control.GridCell
import java.io.File

class ImageCell : GridCell<File>() {
    private val imageView = ImageView()

    init {
        graphic = imageView
    }

    override fun updateItem(item: File?, empty: Boolean) {
        imageView.update(item)
    }
}
