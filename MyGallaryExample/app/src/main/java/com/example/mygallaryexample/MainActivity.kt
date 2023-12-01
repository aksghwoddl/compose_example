package com.example.mygallaryexample

import android.Manifest
import android.app.Application
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.pager.HorizontalPagerIndicator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()
            var granted by remember {
                mutableStateOf(false)
            }
            val launcher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { isGranted ->
                    granted = isGranted
                }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }

            if (granted) {
                Text(text = "권한 허용됨")
                HomeScreen(photoUris = viewModel.photoUris.value)
            } else {
                PermissionRequestScreen {
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoUris = mutableStateOf(emptyList<Uri>())
    val photoUris: State<List<Uri>> = _photoUris

    fun fetchPhotos() {
        val uris = mutableListOf<Uri>()

        getApplication<Application>().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC",
        )?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(columnIndex)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id,
                )
                uris.add(contentUri)
            }
        }
        _photoUris.value = uris
    }
}

@Composable
fun PermissionRequestScreen(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "권한이 허용되지 않았습니다.")
        Button(onClick = onClick) {
            Text(text = "권한 요청")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(photoUris: List<Uri>) {
    val pagerState = rememberPagerState(pageCount = {
        photoUris.size
    })

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .fillMaxSize(),
        ) { pageIndex ->
            Card() {
                GlideImage(model = photoUris[pageIndex], contentDescription = null)
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = photoUris.size,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
        )
    }
}
