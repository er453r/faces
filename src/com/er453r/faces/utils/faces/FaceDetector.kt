package com.er453r.faces.utils.faces

import javafx.geometry.Rectangle2D
import org.bytedeco.opencv.opencv_core.Mat

interface FaceDetector {
    fun findFaces(image: Mat): Set<Rectangle2D>
}
