package com.er453r.faces.components

import tornadofx.ListCellFragment
import tornadofx.onChange
import java.io.File

class ImageFragment : ListCellFragment<File>() {
    private val imageView = ImageView()

    override val root = imageView

    override fun onDock() {
        itemProperty.onChange {
            imageView.update(it)
        }

        imageView.resize(128.0, 128.0)

        imageView.update(item)
    }
}
