package com.muratdayan.lessonline.presentation.features.main.post

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor() : ViewModel() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    fun checkGalleryPermission(context: Context,permissionLauncher: ActivityResultLauncher<Array<String>>, contentLauncher: ActivityResultLauncher<String>) {
        if (hasGalleryPermission(context)){
            openGallery(contentLauncher)
        }else{
            requestGalleryPermission(permissionLauncher)
        }
    }

    fun checkCameraPermission(fragment: Fragment,context: Context,launcher: ActivityResultLauncher<String>,previewView: PreviewView) {
        if (hasCameraPermission(context)){
            startCamera(fragment, context,previewView)
        }else{
            requestCameraPermission(launcher)
        }
    }

    private fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.CAMERA)
    }


    fun startCamera(fragment: Fragment,context:Context,previewView: PreviewView){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(fragment,cameraProvider,previewView)
        }, ContextCompat.getMainExecutor(context))
    }


    private fun bindPreview(fragment:Fragment, cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(fragment,cameraSelector,preview,imageCapture)
        }catch(e:Exception) {
            //Toast.makeText(requireContext(),"Camera cant start", Toast.LENGTH_SHORT).show()
        }
    }

    fun shutdownExecutor() {
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    private fun hasGalleryPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ) {
                return  true
            } else if (ContextCompat.checkSelfPermission(context, READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                return true
            }
            else{
                return false
            }
        }else{
            return true
        }
    }

    private fun requestGalleryPermission(launcher: ActivityResultLauncher<Array<String>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            launcher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            launcher.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }

    fun openGallery(launcher: ActivityResultLauncher<String>){
        launcher.launch("image/*")
    }

    fun handleGalleryResult(uri: Uri?, context: Context) {
        uri?.let {
            // Uri ile resmi işleyebilirsiniz
            Toast.makeText(context, "Resim başarıyla seçildi: $uri", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Resim seçilemedi", Toast.LENGTH_SHORT).show()
        }
    }

    fun takePhoto(context:Context,onImageCaptured: (Uri?)->Unit){
        val outputOptions = ImageCapture.OutputFileOptions.Builder(createImageFile(context)).build()

        imageCapture.takePicture(outputOptions,ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(outputFileResults.savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("PhotoViewModel",exception.message.toString())
                }

            })

    }

    private fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile("JPEG_${System.currentTimeMillis()}_",".jpg",storageDir)
    }

}