package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class VertexViewModel(
    x: Dp = 0.dp,
    y: Dp = 0.dp,
    color: Color,
    val graphViewModel: GraphViewModel,
    private val v: Long,
    private val _valueVisible: State<Boolean>,
    radius: Dp,
) {
    private var _x = mutableStateOf(x)
    var x: Dp
        get() = _x.value
        set(value) {
            _x.value = value
        }
    private var _y = mutableStateOf(y)
    var y: Dp
        get() = _y.value
        set(value) {
            _y.value = value
        }
    private var _color = mutableStateOf(color)
    var color: Color
        get() = _color.value
        set(value) {
            _color.value = value
        }
    private var _radius = mutableStateOf(radius)
    var radius: Dp
        get() = _radius.value
        set(value) {
            _radius.value = value
        }

    val value
        get() = v

    val valueVisible
        get() = _valueVisible.value

    fun onDrag(offset: Offset) {
        _x.value += offset.x.dp
        _y.value += offset.y.dp
    }

    fun onClick()  {
        if (graphViewModel.findPathState) {
            graphViewModel.addVertexToFindPath(this.v)
        }
    }
}
