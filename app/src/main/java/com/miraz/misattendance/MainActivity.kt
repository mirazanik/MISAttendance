package com.miraz.misattendance

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.miraz.misattendance.databinding.ActivityMainBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = findViewById(R.id.previewView)
        val captureButton: Button = findViewById(R.id.captureButton)

        cameraExecutor = Executors.newSingleThreadExecutor()



        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(REQUIRED_PERMISSIONS)
        }

        captureButton.setOnClickListener { takePhoto() }


        binding.visitList.setOnClickListener {
            val intent = Intent(this, VisitActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Operation was successful.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("CameraXApp", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File(getOutputDirectory(), "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraXApp", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Toast.makeText(baseContext, "Photo capture succeeded: $savedUri", Toast.LENGTH_SHORT).show()
                    showStaffInfoDialog(photoFile)
                }
            }
        )
    }

    private fun showStaffInfoDialog(photoFile: File) {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_staff_info, null)
        val etName: EditText = dialogView.findViewById(R.id.etName)
        val etStaffId: EditText = dialogView.findViewById(R.id.etStaffId)
        val etDepartment: EditText = dialogView.findViewById(R.id.etDepartment)
        val etDesignation: EditText = dialogView.findViewById(R.id.etDesignation)
        val btnSubmit: Button = dialogView.findViewById(R.id.btnSubmit)


        etName.setText("test11")
        etStaffId.setText("123456789")
        etDepartment.setText("MIS")
        etDesignation.setText("SE")
        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Set the submit button click listener
        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val staffId = etStaffId.text.toString()
            val department = etDepartment.text.toString()
            val designation = etDesignation.text.toString()

            // Handle the input values here, e.g., save to database, update UI, etc.
            handleStaffInfo(name, staffId, department, designation, photoFile)

            // Dismiss the dialog
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }


    private fun handleStaffInfo(name: String, staffId: String, department: String, designation: String,file: File) {
        // Handle the input values, e.g., save to database, update UI, etc.

        insertToAIServer(
            UploadImageAIServerREQ(
                name =  name,
                staff_id = staffId,
                department = department,
                designation = designation,
                image = file
            )
        )
    }



    private fun insertToAIServer(
        uploadImageAIServerREQ: UploadImageAIServerREQ
    ) {

        ApiServices.uploadImageEmbToAIServer(uploadImageAIServerREQ, object : UploadImageEmbListener {
            override fun success(isSuccess: Boolean, message: String) {
                Const().showToast(this@MainActivity, "$message")
                binding.progressBar.visibility= View.GONE
                showSuccessDialog()
            }

            override fun data(imageDatasetRP: ImageDatasetRP) {
                binding.progressBar.visibility= View.GONE
                showSuccessDialog()
            }
        })
    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
