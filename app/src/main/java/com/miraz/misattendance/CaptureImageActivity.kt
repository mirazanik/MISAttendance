package com.miraz.misattendance

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.miraz.misattendance.databinding.ActivityCaptureImageBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CaptureImageActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    lateinit var photoFile: File


    lateinit var binding: ActivityCaptureImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaptureImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.captureButton.setOnClickListener { takePhoto() }
        binding.btnCameraRotate.setOnClickListener { rotateCamera() }

        binding.visitList.setOnClickListener {
            val intent = Intent(this, VisitActivity::class.java)
            startActivity(intent)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                // Permission denied
            }
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.Builder().requireLensFacing(lensFacing).build(),
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("MainActivity", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        photoFile = File(
            externalMediaDirs.firstOrNull(),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("MainActivity", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
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
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

        etName.setText("test11")
        etStaffId.setText("123456789")
        etDepartment.setText("MIS")
        etDesignation.setText("SE")
        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
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
        // Set the submit button click listener
        btnCancel.setOnClickListener {

            photoFile.delete()
            // Dismiss the dialog
            dialog.dismiss()
        }


        // Show the dialog
        dialog.show()

    }

    private fun handleStaffInfo(
        name: String,
        staffId: String,
        department: String,
        designation: String,
        file: File
    ) {
        // Handle the input values, e.g., save to database, update UI, etc.

        insertToAIServer(
            UploadImageAIServerREQ(
                name = name,
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

        ApiServices.uploadImageEmbToAIServer(
            uploadImageAIServerREQ,
            object : UploadImageEmbListener {
                override fun success(isSuccess: Boolean, message: String, regRP: RegRP) {
                    Const().showToast(this@CaptureImageActivity, "$message")
                    binding.progressBar.visibility = View.GONE
                    showSuccessDialog(regRP)
                }
            })
    }

    private fun showSuccessDialog(regRP: RegRP) {


        photoFile.delete()
        Log.d("MainActivity", "Photo deleted: ${photoFile.absolutePath}")


        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Status: ${regRP.status_code}")
        builder.setTitle("")
        builder.setMessage(regRP.message)
        builder.setPositiveButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("View List") { dialog, _ ->

            val intent = Intent(this, VisitActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun rotateCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }

        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
