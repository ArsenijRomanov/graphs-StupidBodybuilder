import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.layout.onSizeChanged

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun ZoomableBox(content: @Composable BoxScope.() -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val minScale = 0.1f
    val maxScale = 5f

    var boxWidth by remember { mutableStateOf(0f) }
    var boxHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                boxWidth = it.width.toFloat()
                boxHeight = it.height.toFloat()
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                    val centerX = boxWidth / 2f
                    val centerY = boxHeight / 2f

                    offsetX = (offsetX - centerX) * (newScale / scale) + centerX + pan.x
                    offsetY = (offsetY - centerY) * (newScale / scale) + centerY + pan.y

                    scale = newScale
                }
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val scrollDelta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                        if (scrollDelta != 0f) {
                            val zoomFactor = if (scrollDelta < 0) 1.1f else 0.9f
                            val newScale = (scale * zoomFactor).coerceIn(minScale, maxScale)

                            val centerX = boxWidth / 2f
                            val centerY = boxHeight / 2f

                            offsetX = (offsetX - centerX) * (newScale / scale) + centerX
                            offsetY = (offsetY - centerY) * (newScale / scale) + centerY

                            scale = newScale
                        }
                    }
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        content()
    }
}



