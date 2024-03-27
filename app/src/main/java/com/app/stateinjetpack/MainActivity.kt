package com.app.stateinjetpack

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.stateinjetpack.ui.theme.StateInJetpackTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateInJetpackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   Derived()
                }
            }
        }
    }
}

@Composable
fun Derived() {
    val tableOf = remember { mutableIntStateOf(5) }
    val index = produceState(initialValue = 1) {
        repeat(9) {
            delay(1000)
            value += 1
        }
    }
    val answer = derivedStateOf {
        "${tableOf.intValue} * ${index.value} = ${tableOf.intValue * index.value}"
    }
    Text(text = answer.value, style = MaterialTheme.typography.labelLarge)
}

@Composable
fun Loader() {
    val state = produceState(initialValue = 0) {
        while (true) {
            delay(16)
            value = (value + 16) % 360
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(1f), content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(state.value.toFloat())
                )
                Text(text = "Loading")
            }
        })
}

@Composable
fun Music() {
    val context = LocalContext.current
    DisposableEffect(key1 = Unit) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.music)
        mediaPlayer.start()

        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}

@Composable
fun App() {
    val state = remember { mutableStateOf(false) }

    DisposableEffect(key1 = state.value) {
        Log.d("TAG", "Disposable effect is Started.")
        onDispose {
            Log.d("TAG", "Cleaning up the side effects.")
        }
    }
    Button(onClick = { state.value = !state.value }) {
        Text(text = "Change State")
    }
}

@Composable
fun Counter() {
    val counter = remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        delay(2000)
        counter.intValue = 10
        Log.d("TAG", counter.intValue.toString())
    }
    ChangeCounter(value = counter.intValue)
}

@Composable
fun ChangeCounter(value: Int) {
    val state = rememberUpdatedState(newValue = value)
    LaunchedEffect(key1 = Unit) {
        delay(5000)
        Log.d("TAG", state.value.toString())
    }
}


@Composable
fun Greeting() {
    val counter = remember {
        mutableIntStateOf(0)
    }
    val scope = rememberCoroutineScope()

    var text = "counter is running ${counter.intValue}"
    if (counter.intValue == 10) {
        text = "Counter is Stopped."
    }
    Column {
        Text(text = text)
        Button(onClick = {
            scope.launch {
                Log.d("TAG", "Started...")
                try {
                    for (i in 1..10) {
                        counter.intValue++
                        delay(1000)
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "Exception - ${e.localizedMessage}")
                }
            }
        }) {
            Text(text = "Start")
        }
    }
}