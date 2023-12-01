package com.example.stopwatchexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()
            val sec = viewModel.sec.value
            val milliSec = viewModel.milliSec.value
            val isRunning = viewModel.isRunning.value
            val lapTimes = viewModel.lapTimes.value

            MainScreen(
                sec = sec,
                milliSec = milliSec,
                isRunning = isRunning,
                lapTimes = lapTimes,
                onReset = {
                    viewModel.reset()
                },
                onToggle = { running ->
                    if (running) {
                        viewModel.pause()
                    } else {
                        viewModel.start()
                    }
                },
                onLapTime = {
                    viewModel.recordLapTime()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    sec: Int,
    milliSec: Int,
    isRunning: Boolean,
    lapTimes: List<String>,
    onReset: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onLapTime: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "StopWatch")
            })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Row() {
                    Text(
                        text = "$sec",
                        fontSize = 100.sp,
                    )
                    Text(
                        text = "$milliSec",
                        modifier = Modifier.align(Alignment.Bottom),
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    content = {
                        items(lapTimes) { lapTime ->
                            Text(text = lapTime)
                        }
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FloatingActionButton(
                        onClick = { onReset() },
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_reset_24),
                            contentDescription = "reset",
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            onToggle(isRunning)
                        },
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isRunning) R.drawable.ic_pause_24 else R.drawable.ic_play_24,
                            ),
                            contentDescription = if (isRunning) "pause" else "play",
                        )
                    }

                    Button(onClick = {
                        onLapTime()
                    }) {
                        Text(text = "랩타임")
                    }
                }
            }
        },
    )
}

class MainViewModel : ViewModel() {
    private var time = 0

    private var timerTask: Timer? = null

    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> = _isRunning

    private val _sec = mutableStateOf(0)
    val sec: State<Int> = _sec

    private val _milliSec = mutableStateOf(0)
    val milliSec: State<Int> = _milliSec

    private val _lapTimes = mutableStateOf(listOf<String>())
    val lapTimes: State<List<String>> = _lapTimes

    private var lap = 1

    fun start() {
        _isRunning.value = true
        timerTask = timer(period = 10) {
            time++
            _sec.value = time / 100
            _milliSec.value = time % 100
        }
    }

    fun pause() {
        _isRunning.value = false
        timerTask?.cancel()
    }

    fun reset() {
        timerTask?.cancel()
        time = 0
        _sec.value = 0
        _milliSec.value = 0
        lap = 0
        _lapTimes.value = emptyList()
        _isRunning.value = false
    }

    fun recordLapTime() {
        val lapTimes = ArrayList(_lapTimes.value)
        lapTimes.add(0, "$lap LAP : ${sec.value}.${milliSec.value}")
        _lapTimes.value = lapTimes.toList()
        lap++
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen(
        sec = 8,
        milliSec = 11,
        isRunning = true,
        lapTimes = emptyList(),
        onReset = { },
        onToggle = { },
        onLapTime = { },
    )
}
