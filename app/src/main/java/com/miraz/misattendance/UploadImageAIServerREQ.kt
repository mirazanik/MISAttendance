package com.miraz.misattendance

import java.io.File

data class UploadImageAIServerREQ(
    val name: String,
    val staff_id: String,
    val department: String = "Yamaha3S",
    val designation: String = "Customer",
    val image: File
)