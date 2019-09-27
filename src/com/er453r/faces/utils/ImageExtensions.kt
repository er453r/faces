package com.er453r.faces.utils

import javafx.scene.image.Image
import org.bytedeco.javacv.JavaFXFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Size
import java.io.File
import kotlin.math.max

fun File.image(maxSize: Int = 128): Image {
    val javaFXFrameConverter = JavaFXFrameConverter() // has to be in the function context for multi-threaded work
    val openCVFrameConverter = OpenCVFrameConverter.ToIplImage() // has to be in the function context for multi-threaded work

    val img = opencv_imgcodecs.imread(path)
    val scale = maxSize.toDouble() / max(img.arrayWidth(), img.arrayHeight())
    val resizedImage = Mat()

    opencv_imgproc.resize(
        img,
        resizedImage,
        Size((img.arrayWidth() * scale).toInt(), (img.arrayHeight() * scale).toInt())
    )

    val frame = openCVFrameConverter.convert(resizedImage)

    return javaFXFrameConverter.convert(frame)
}
