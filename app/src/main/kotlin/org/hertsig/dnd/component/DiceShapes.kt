package org.hertsig.dnd.component

import androidx.compose.ui.graphics.Shape
import org.hertsig.compose.ScaledShape
import org.hertsig.compose.polygon

object DiceShapes {
    val D4: Shape = ScaledShape {
        moveTo(.5f, 0f)
        lineTo(1f, .9f)
        lineTo(0f, .9f)
    }

    val D6: Shape = ScaledShape {
        moveTo(.1f, .1f)
        lineTo(.9f, .1f)
        lineTo(.9f, .9f)
        lineTo(.1f, .9f)
    }

    val D8: Shape = polygon(4)

    val D10: Shape = ScaledShape {
        moveTo(.5f, 0f)
        lineTo(1f, .66f)
        lineTo(.5f, 1f)
        lineTo(0f, .66f)
    }

    val D12: Shape = ScaledShape {
        moveTo(.5f, 0f)
        lineTo(1f, .45f)
        lineTo(.8f, .95f)
        lineTo(.2f, .95f)
        lineTo(0f, .45f)
    }

    val D20: Shape = polygon(6, 30f)
}
