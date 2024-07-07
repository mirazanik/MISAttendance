package com.miraz.misattendance

/**
 * Created by Md Miraz Hossain on 20-Feb-24.
 * miraz.anik@gmail.com
 */

interface UploadImageEmbListener {
    fun success(isSuccess: Boolean, message: String)
    fun data(imageDatasetRP: ImageDatasetRP)

}