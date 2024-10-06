package com.muratdayan.lessonline.presentation.features.main.post

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor() : ViewModel() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    fun checkGalleryPermission(context: Context,permissionLauncher: ActivityResultLauncher<String>, contentLauncher: ActivityResultLauncher<String>) {
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

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.CAMERA)
    }


    fun startCamera(fragment: Fragment,context:Context,previewView: PreviewView){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(fragment,cameraProvider,previewView)
        }, ContextCompat.getMainExecutor(context))
    }


    fun bindPreview(fragment:Fragment,cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
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

    fun hasGalleryPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestGalleryPermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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

}