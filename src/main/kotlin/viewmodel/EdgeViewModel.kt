package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.Edge

class EdgeViewModel(
    color: Color,
    width: Float,
    val firstVertex: VertexViewModel,
    val secondVertex: VertexViewModel,
    private val e: Edge,
    private val _weightVisible: State<Boolean>,
) {
    private var _color = mutableStateOf(color)
    var color: Color
        get() = _color.value
        set(value) {
            _color.value = value
        }

    private var _width = mutableStateOf(width)
    var width: Float
        get() = _width.value
        set(value) {
            _width.value = value
        }

    val weight
        get() = e.weight.toString()

    val weightVisible
        get() = _weightVisible.value
}
