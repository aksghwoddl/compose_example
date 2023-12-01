package com.example.fatcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<BmiViewModel>()
            val navController = rememberNavController()
            val bmi = viewModel.result.value

            NavHost(navController = navController, startDestination = "home") {
                composable(route = "home") {
                    HomeScreen { height, weight ->
                        viewModel.calculateBmi(
                            height = height,
                            weight = weight,
                        )
                        navController.navigate("result")
                    }
                }
                composable(route = "result") {
                    ResultScreen(navController, result = bmi)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onResultClicked: (Double, Double) -> Unit) {
    val (height, setHeight) = rememberSaveable {
        mutableStateOf("")
    }

    val (weight, setWeight) = rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "비만도 계산기")
            })
        },

        content = { values ->
            Column(
                modifier = Modifier.padding(values),
            ) {
                OutlinedTextField(
                    value = height,
                    onValueChange = setHeight,
                    label = { Text(text = "키") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = setWeight,
                    label = { Text(text = "몸무게") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (weight.isNotEmpty() && height.isNotEmpty()) {
                            onResultClicked(height.toDouble(), weight.toDouble())
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(text = "결과")
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, result: Double) {
    val text = when {
        result >= 35 -> {
            "고도비만"
        }

        result >= 30 -> {
            "2단계 비만"
        }

        result >= 25 -> {
            "1단계 비만"
        }

        result >= 23 -> {
            "과체중"
        }

        result >= 18.5 -> {
            "정상"
        }

        else -> {
            "저체중"
        }
    }

    val icon = when {
        result >= 23 -> {
            R.drawable.ic_over_wieght_24
        }

        result >= 18.5 -> {
            R.drawable.ic_normal_weight_24
        }

        else -> {
            R.drawable.ic_low_weight_24
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "비만도 계산기") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navController.navigateUp()
                        }.padding(end = 10.dp),
                    )
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues = paddingValues).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    fontSize = 30.sp,
                )
                Spacer(modifier = Modifier.height(50.dp))
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(Color.Black),
                )
            }
        },
    )
}

@Preview
@Composable
fun PreviewResultScreen() {
    // ResultScreen(result = 0.0)
}

class BmiViewModel() : ViewModel() {
    private val _result = mutableStateOf(0.0)
    val result: State<Double> = _result

    fun calculateBmi(height: Double, weight: Double) {
        _result.value = weight / (height / 100.0).pow(2.0)
    }
}
