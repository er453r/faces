package com.er453r.faces.utils.faces.detectors

import com.er453r.faces.utils.faces.FaceDetector
import javafx.geometry.Rectangle2D
import org.bytedeco.javacpp.indexer.FloatIndexer
import org.bytedeco.opencv.global.opencv_core.CV_32F
import org.bytedeco.opencv.global.opencv_dnn.blobFromImage
import org.bytedeco.opencv.global.opencv_dnn.readNetFromCaffe
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.global.opencv_imgproc.resize
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_dnn.Net
import org.opencv.imgproc.Imgproc

class DeepLearningFaceDetector : FaceDetector {
    companion object {
        private val PROTO_FILE = "deploy.prototxt.txt"
        private val CAFFE_MODEL_FILE = "res10_300x300_ssd_iter_140000.caffemodel"
        private var net: Net? = readNetFromCaffe("/home/mkotz/IdeaProjects/family/faces/res/$PROTO_FILE", "/home/mkotz/IdeaProjects/family/faces/res/$CAFFE_MODEL_FILE")
    }

    override fun findFaces(source: Mat): Set<Rectangle2D> {
        val originalWidth = source.arrayWidth()
        val originalHeight = source.arrayHeight()

        val image = Mat() // convert to grayscale and equalize histogram for better detection
        opencv_imgproc.cvtColor(source, image, Imgproc.COLOR_RGBA2RGB)

        //create a 4-dimensional blob from image with NCHW (Number of images in the batch -for training only-, Channel, Height, Width) dimensions order,
        //for more detailes read the official docs at https://docs.opencv.org/trunk/d6/d0f/group__dnn.html#gabd0e76da3c6ad15c08b01ef21ad55dd8
        val blob = blobFromImage(image, 1.0, Size(300, 300), Scalar(104.0, 177.0, 123.0, 0.0), false, false, CV_32F)

        net!!.setInput(blob)//set the input to network model
        val output = net!!.forward()//feed forward the input to the netwrok to get the output matrix

        val ne = Mat(Size(output.size(3), output.size(2)), CV_32F, output.ptr(0, 0))//extract a 2d matrix for 4d output matrix with form of (number of detections x 7)

        val srcIndexer = ne.createIndexer<FloatIndexer>() // create indexer to access elements of the matric

        val facesSet = mutableSetOf<Rectangle2D>()

        for (i in 0 until output.size(3)) {//iterate to extract elements
            val confidence = srcIndexer.get(i.toLong(), 2)
            val f1 = srcIndexer.get(i.toLong(), 3)
            val f2 = srcIndexer.get(i.toLong(), 4)
            val f3 = srcIndexer.get(i.toLong(), 5)
            val f4 = srcIndexer.get(i.toLong(), 6)
            if (confidence > .6) {
                val tx = f1 * 300.0//top left point's x
                val ty = f2 * 300.0//top left point's y
                val bx = f3 * 300.0//bottom right point's x
                val by = f4 * 300.0//bottom right point's y

                val width = bx - tx
                val height = by - ty

                if(width > 0 && height > 0)
                    facesSet.add(
                        Rectangle2D(
                            tx /  originalWidth,
                            ty / originalHeight,
                            width / originalWidth,
                            height / originalHeight
                        )
                    )
            }
        }

        return facesSet
    }
}
