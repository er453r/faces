package com.er453r.faces.utils

import javafx.geometry.Rectangle2D
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.RectVector
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier
import org.opencv.imgproc.Imgproc

class FaceDetector {
    private val faceDetector = CascadeClassifier("/home/mkotz/IdeaProjects/family/faces/res/haarcascade_frontalface_alt2.xml")

    fun findFaces(image: Mat, relative: Boolean = false): Set<Rectangle2D> {
        val gray = Mat() // convert to grayscale and equalize histograe for better detection
        opencv_imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)
        opencv_imgproc.equalizeHist(gray, gray)

        // Find faces on the image
        val faces = RectVector()

        faceDetector.detectMultiScale(gray, faces)

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
