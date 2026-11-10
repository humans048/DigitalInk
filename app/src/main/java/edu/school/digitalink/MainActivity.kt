package edu.school.digitalink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val inkViewModel: InkViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF6750A4),
                background = Color(0xFFFFFBFE),
                surface = Color(0xFFFFFBFE)
            )) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DigitalInk(inkViewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DigitalInk(viewModel: InkViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val canvasSize = minOf(300.dp, maxWidth - 32.dp, (maxHeight - 150.dp).coerceAtLeast(100.dp))
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 與課件一致：300dp 正方形、灰框、黑色筆跡。
        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .border(2.dp, Color.Gray)
                .clipToBounds()
                .semantics { contentDescription = "手寫畫布，可用手指或滑鼠拖曳寫字" }
                .pointerInput(viewModel) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        fun point(position: Offset) = Offset(
                            position.x.coerceIn(0f, size.width.toFloat()) / size.width * 1000f,
                            position.y.coerceIn(0f, size.height.toFloat()) / size.height * 1000f
                        )
                        val first = point(down.position)
                        viewModel.start(first.x, first.y, down.uptimeMillis)
                        try {
                            var pressed = true
                            while (pressed) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id }
                                if (change == null) {
                                    viewModel.cancel()
                                    break
                                }
                                change.historical.forEach {
                                    val p = point(it.position)
                                    viewModel.move(p.x, p.y, it.uptimeMillis)
                                }
                                val p = point(change.position)
                                viewModel.move(p.x, p.y, change.uptimeMillis)
                                change.consume()
                                pressed = change.pressed
                                if (!pressed) viewModel.finish()
                            }
                        } finally {
                            // 指標中斷或畫面旋轉時，保留已寫出的這一筆。
                            viewModel.finish()
                        }
                    }
                }
        ) {
            val width = 10f
            viewModel.strokes.forEach { stroke ->
                if (stroke.size == 1) {
                    val p = stroke.first()
                    drawCircle(Color.Black, width / 2f, Offset(p.x * size.width / 1000f, p.y * size.height / 1000f))
                } else {
                    stroke.zipWithNext { a, b ->
                        drawLine(
                            Color.Black, Offset(a.x * size.width / 1000f, a.y * size.height / 1000f), Offset(b.x * size.width / 1000f, b.y * size.height / 1000f),
                            strokeWidth = width, cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = viewModel::recognize, enabled = !viewModel.busy) {
                Text(if (viewModel.busy) "辨識中…" else "開始辨識")
            }
            Button(onClick = viewModel::clear) { Text("清除") }
        }
        Spacer(Modifier.height(16.dp))
        Text(viewModel.message, textAlign = TextAlign.Center)
    }
}
}
