package com.pantrypilot.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pantrypilot.app.R
import com.pantrypilot.app.util.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PantryCameraScreen(
    onPhotoCaptured: (File) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
        }

    var lensFacing by remember {
        mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
    }

    var torchEnabled by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var camera by remember {
        mutableStateOf<Camera?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isCapturing = true
            cameraError = null
            scope.launch(Dispatchers.IO) {
                try {
                    val tempFile = File(
                        context.cacheDir,
                        "pantrypilot_gallery_${System.currentTimeMillis()}.jpg"
                    )
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val compressedFile = ImageCompressor.compressImageFile(
                        sourceFile = tempFile,
                        maxDimension = 1024,
                        quality = 80
                    )
                    withContext(Dispatchers.Main) {
                        isCapturing = false
                        onPhotoCaptured(compressedFile)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isCapturing = false
                        cameraError = context.getString(R.string.camera_error_gallery_load)
                    }
                }
            }
        }
    }

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(
        hasPermission,
        lifecycleOwner,
        lensFacing
    ) {
        var provider: ProcessCameraProvider? = null
        var disposed = false

        if (hasPermission) {
            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                if (!disposed) {
                    try {
                        val cameraProviderInstance =
                            cameraProviderFuture.get()

                        provider = cameraProviderInstance

                        val preview = Preview.Builder()
                            .build()
                            .also {
                                it.setSurfaceProvider(
                                    previewView.surfaceProvider
                                )
                            }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(
                                ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                            )
                            .build()

                        val selector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        cameraProviderInstance.unbindAll()

                        val boundCamera =
                            cameraProviderInstance.bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                capture
                            )

                        imageCapture = capture
                        camera = boundCamera
                        cameraError = null

                    } catch (exception: Exception) {
                        cameraError = context.getString(R.string.camera_error_preview)
                    }
                }
            }, ContextCompat.getMainExecutor(context))
        }

        onDispose {
            disposed = true
            provider?.unbindAll()
            camera = null
            imageCapture = null
        }
    }

    LaunchedEffect(lensFacing) {
        torchEnabled = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasPermission) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.12f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .fillMaxHeight(0.42f)
                    .align(Alignment.Center)
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(22.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraIconControlButton(
                    icon = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_close),
                    onClick = onClose
                )

                CameraIconControlButton(
                    icon = if (torchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = if (torchEnabled) stringResource(R.string.cd_turn_flash_off) else stringResource(R.string.cd_turn_flash_on),
                    tint = if (torchEnabled) Color(0xFFFFD54F) else Color.White,
                    onClick = {
                        val next = !torchEnabled
                        camera?.cameraControl?.enableTorch(next)
                        torchEnabled = next
                    }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.camera_position_hint),
                    color = Color.White,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CameraIconControlButton(
                        icon = Icons.Default.FlipCameraAndroid,
                        contentDescription = stringResource(R.string.cd_switch_camera),
                        onClick = {
                            lensFacing =
                                if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .border(
                                3.dp,
                                Color.White,
                                CircleShape
                            )
                            .padding(6.dp)
                            .background(
                                Color.White,
                                CircleShape
                            )
                            .clickable(
                                enabled = !isCapturing && imageCapture != null
                            ) {
                                val capture = imageCapture ?: return@clickable

                                isCapturing = true
                                cameraError = null

                                val photoFile = File(
                                    context.cacheDir,
                                    "pantrypilot_${System.currentTimeMillis()}.jpg"
                                )

                                val outputOptions =
                                    ImageCapture.OutputFileOptions
                                        .Builder(photoFile)
                                        .build()

                                capture.takePicture(
                                    outputOptions,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(
                                            outputFileResults: ImageCapture.OutputFileResults
                                        ) {
                                            scope.launch(Dispatchers.IO) {
                                                val compressedFile = ImageCompressor.compressImageFile(
                                                    sourceFile = photoFile,
                                                    maxDimension = 1024,
                                                    quality = 80
                                                )
                                                withContext(Dispatchers.Main) {
                                                    isCapturing = false
                                                    onPhotoCaptured(compressedFile)
                                                }
                                            }
                                        }

                                        override fun onError(
                                            exception: ImageCaptureException
                                        ) {
                                            isCapturing = false
                                            cameraError = context.getString(R.string.camera_error_capture)
                                        }
                                    }
                                )
                            }
                    )

                    CameraIconControlButton(
                        icon = Icons.Default.PhotoLibrary,
                        contentDescription = stringResource(R.string.cd_pick_gallery),
                        onClick = {
                            if (!isCapturing) {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                    )
                }

                if (isCapturing) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                cameraError?.let { errorText ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .background(Color(0xD9B00020), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = errorText,
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.camera_permission_required),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.camera_permission_desc),
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        permissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                ) {
                    Text(stringResource(R.string.camera_permission_allow))
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.camera_permission_pick_gallery), color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onClose) {
                    Text(stringResource(R.string.camera_go_back), color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
private fun CameraIconControlButton(
    icon: ImageVector,
    contentDescription: String?,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                Color.Black.copy(alpha = 0.45f),
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
