package dev.baseio.composeplayground.ui.animations.anmolverma

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DribbleCircleTouch() {
    var isOpen by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isOpen = !isOpen
            }
            .background(Color(31, 34, 43))
    ) {

        CenterCircle(isOpen, Modifier.align(Alignment.Center))
        OuterCircle(isOpen, Modifier.align(Alignment.Center))

        RandomParticles(isOpen, Modifier.align(Alignment.Center))

    }

}

@Composable
fun RandomParticles(open: Boolean, modifier: Modifier) {
    var offsetCenter = Offset.Zero

    Canvas(modifier = Modifier, onDraw = {
        offsetCenter = center
    })

    val particles by remember {
        mutableStateOf(dribbleCircleParticles(offsetCenter))
    }

    particles.map {
        DrawParticle(open, modifier, it)
    }
}

val random = Random

@Composable
fun DrawParticle(open: Boolean, modifier: Modifier, dribbleParticle: DribbleParticle) {

    val animateX = remember {
        Animatable(dribbleParticle.posX)
    }

    val rotation = remember {
        Animatable(0f)
    }

    val animateY = remember {
        Animatable(dribbleParticle.posY)
    }


    val isVisible = remember {
        Animatable(if (open) 1f else 0f)
    }

    LaunchedEffect(key1 = open, block = {

        launch {
            val finalValue =
                (dribbleParticle.posX + cos(Math.toRadians(dribbleParticle.angle.toDouble())) * 1500f).toFloat()
            animateX.animateTo(
                if(!open) dribbleParticle.posX else finalValue,
                tween(3000, easing = LinearEasing)
            )
        }
        launch {
            val finalValue =
                (dribbleParticle.posY + sin(Math.toRadians(dribbleParticle.angle.toDouble())) * 1500f).toFloat()
            animateY.animateTo(
                if(!open) dribbleParticle.posY else  finalValue,
                tween(3000, easing = LinearEasing)
            )
        }
        launch {
            isVisible.snapTo(if (open) 1f else 0f)
        }
        launch {
            rotation.animateTo(360f, tween(3000))
        }
    })

    Box(
        modifier = modifier
            .alpha(isVisible.value)
            .rotate(rotation.value)
            .offset {
                IntOffset(animateX.value.toInt(), animateY.value.toInt())
            }
            .border(
                shape = dribbleParticle.shape,
                color = dribbleParticle.color,
                width = dribbleParticle.size
            )
            .size(dribbleParticle.shapeSize)
    )
}

val colors = mutableListOf<Color>().apply {
    add(Color(202, 141, 149))
    add(Color(197, 65, 80))
    add(Color(190, 64, 79))
}
val shapes = mutableListOf<Shape>().apply {
    add(GenericShape { size, _ ->
        // 1)
        moveTo(size.width / 2f, 0f)

        // 2)
        lineTo(size.width, size.height)

        // 3)
        lineTo(0f, size.height)
    })
    add(CircleShape)
    add(RoundedCornerShape(0))
}

val sizes = mutableListOf<Int>().apply {
    add(16)
    add(20)
    add(24)
}


@Composable
fun OuterCircle(openRequested: Boolean, modifier: Modifier) {
    val circleSize by animateDpAsState(
        targetValue = if (openRequested) 90.dp else 70.dp,
        animationSpec = springSpec()
    )

    Box(
        modifier = modifier
            .background(Color.Transparent, shape = CircleShape)
            .border(2.dp, Color(219, 72, 88), CircleShape)
            .size(circleSize)
    )
}

@Composable
fun CenterCircle(openRequested: Boolean, modifier: Modifier) {
    val circleSize by animateDpAsState(
        targetValue = if (openRequested) 70.dp else 50.dp,
        animationSpec = springSpecCenterCircle()
    )

    Box(
        modifier = modifier
            .background(Color(219, 72, 88), shape = CircleShape)
            .size(circleSize)
    )
}

private fun springSpec(): SpringSpec<Dp> =
    spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessLow
    )

private fun springSpecCenterCircle(): SpringSpec<Dp> =
    spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )


fun dribbleCircleParticles(centerOffset: Offset): List<DribbleParticle> =
    mutableListOf<DribbleParticle>().apply {
        var angle = 0f
        repeat(10) {
            add(
                DribbleParticle(
                    centerOffset.x,
                    centerOffset.y,
                    angle,
                    shapes[random.nextInt(0, 3)],
                    colors[random.nextInt(0, 3)],
                    random.nextDouble(0.5, 2.5).dp,
                    sizes[random.nextInt(0, 3)].dp
                )
            )
            angle += 36
        }
    }

data class DribbleParticle(
    val posX: Float,
    val posY: Float,
    var angle: Float,
    val shape: Shape,
    val color: Color,
    val size: Dp,
    val shapeSize: Dp
)

@Preview
@Composable
fun PreviewDribbleCircleTouch() {
    MaterialTheme() {
        DribbleCircleTouch()
    }
}