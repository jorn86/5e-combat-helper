package org.hertsig.dnd.component

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.sin

object Shapes {
    val D4 = GenericShape { size, _ ->
        moveTo(size.width / 2, 0f)
        lineTo(size.width, size.height * .9f)
        lineTo(0f, size.height * .9f)
    }

    val D6 = GenericShape { size, _ ->
        moveTo(size.width * .1f, size.height * .1f)
        lineTo(size.width * .9f, size.height * .1f)
        lineTo(size.width * .9f, size.height * .9f)
        lineTo(size.width * .1f, size.height * .9f)
    }

    val D8 = GenericShape { size, _ ->
        moveTo(size.width / 2, 0f)
        lineTo(size.width, size.height / 2)
        lineTo(size.width / 2, size.height)
        lineTo(0f, size.height / 2)
    }

    val D10 = GenericShape { size, _ ->
        moveTo(size.width / 2, 0f)
        lineTo(size.width, size.height * .66f)
        lineTo(size.width / 2, size.height)
        lineTo(0f, size.height * .66f)
    }

    val D12 = GenericShape { size, _ ->
        moveTo(size.width / 2, 0f)
        lineTo(size.width, size.height * .45f)
        lineTo(size.width * .8f, size.height * .95f)
        lineTo(size.width * .2f, size.height * .95f)
        lineTo(0f, size.height * .45f)
    }

    val D20 = GenericShape { size, _ ->
        moveTo(size.width * .5f, 0f)
        lineTo(size.width * .95f, size.height *.25f)
        lineTo(size.width * .95f, size.height *.75f)
        lineTo(size.width * .5f, size.height)
        lineTo(size.width * .05f, size.height *.75f)
        lineTo(size.width * .05f, size.height *.25f)
    }
}

class Polygon(private val sides: Int, private val rotation: Float = 0f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val radius = if (size.width > size.height) size.width / 2f else size.height / 2f
                val angle = 2.0 * Math.PI / sides
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = rotation * (Math.PI / 180)
                moveTo(
                    cx + (radius * cos(0.0 + r).toFloat()),
                    cy + (radius * sin(0.0 + r).toFloat())
                )
                for (i in 1 until sides) {
                    lineTo(
                        cx + (radius * cos(angle * i + r).toFloat()),
                        cy + (radius * sin(angle * i + r).toFloat())
                    )
                }
                close()
            })
    }
}
