package com.mathewsachin.fategrandautomata.scripts

import com.mathewsachin.fategrandautomata.scripts.modules.Phone
import com.mathewsachin.libautomata.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private sealed class ScaleBy(val rate: Double) : Comparable<ScaleBy> {
    class Width(rate: Double) : ScaleBy(rate)
    class Height(rate: Double) : ScaleBy(rate)

    override fun compareTo(other: ScaleBy) =
        rate.compareTo(other.rate)
}

private fun decideScaleMethod(OriginalSize: Size, DesiredSize: Size) =
    minOf(
        ScaleBy.Width(DesiredSize.Width / OriginalSize.Width.toDouble()),
        ScaleBy.Height(DesiredSize.Height / OriginalSize.Height.toDouble())
    )

private fun calculateBorderThickness(Outer: Int, Inner: Int) =
    ((Outer - Inner).absoluteValue / 2.0).roundToInt()

private fun calculateGameAreaWithoutBorders(
    ScriptSize: Size,
    ScreenSize: Size,
    ScaleRate: Double
): Region {
    val scaledScriptSize = ScriptSize * ScaleRate
    var border = 170
    border = when {
        Phone.s.contains("Pixel 4 XL") -> {
            170
        }
        Phone.s.contains("SM-G975U") -> {
            110
        }
        else -> {
            150
        }
    }
    return Region(
        // jp fullscreen
        border,
//        calculateBorderThickness(
//            ScreenSize.Width,
//            scaledScriptSize.Width
//        ), // Offset(X)
        calculateBorderThickness(
            ScreenSize.Height,
            scaledScriptSize.Height
        ), // Offset(Y)
        scaledScriptSize.Width, // Game Width (without borders)
        scaledScriptSize.Height // Game Height (without borders)
    )
}

class FgoGameAreaManager(
    val platformImpl: IPlatformImpl,
    scriptSize: Size,
    imageSize: Size
) : GameAreaManager {
    private val gameWithBorders = platformImpl.windowRegion
    private val scaleBy = decideScaleMethod(
        scriptSize,
        gameWithBorders.size
    )
    private val gameAreaIgnoringNotch =
        calculateGameAreaWithoutBorders(
            scriptSize,
            gameWithBorders.size,
            scaleBy.rate
        )

    override val scriptDimension = when (scaleBy) {
        is ScaleBy.Width -> CompareBy.Width(scriptSize.Width)
        is ScaleBy.Height -> CompareBy.Height(scriptSize.Height)
    }

    override val compareDimension = when (scaleBy) {
        is ScaleBy.Width -> CompareBy.Width(imageSize.Width)
        is ScaleBy.Height -> CompareBy.Height(imageSize.Height)
    }

    override val gameArea
        get() = gameAreaIgnoringNotch + platformImpl.windowRegion.location
}