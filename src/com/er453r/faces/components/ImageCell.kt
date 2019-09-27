package com.er453r.faces.components

import org.controlsfx.control.GridCell
import java.io.File

class ImageCell : GridCell<File>() {
    override fun updateItem(item: File?, empty: Boolean) {
        graphic = if (empty) {
            null
        } else {
            ImageView(item)
        }
    }
}
