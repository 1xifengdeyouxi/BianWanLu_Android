package com.swu.myapplication.ui.game.erLingSiBagame.ui.tile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.BoardScope
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.Layer
import com.swu.myapplication.ui.game.erLingSiBagame.ui.data.TileModel
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.TileDarkText
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.TileLightText
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.tileBackgroundColors

private const val NEW_DURATION = 150
private const val SWIPE_DURATION = 120
private const val MERGE_DURATION = 200

interface TileRenderer {
    @Composable
    fun BoardScope.RenderNewTile(tileModel: TileModel)

    @Composable
    fun BoardScope.RenderStaticTile(tileModel: TileModel)

    @Composable
    fun BoardScope.RenderSwipedTile(tileModel: TileModel)

    @Composable
    fun BoardScope.RenderMergedTile(tileModel: TileModel)
}

object TileRendererInstance : TileRenderer {

    @Composable
    override fun BoardScope.RenderNewTile(tileModel: TileModel) {
        key(tileModel.id) {
            val scale = remember { Animatable(0f) }
            val alpha = remember { Animatable(0f) }
            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = tileModel.curPosition.row,
                        col = tileModel.curPosition.col,
                        layer = Layer.AnimationLayer
                    )
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            LaunchedEffect(this) {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        delayMillis = SWIPE_DURATION,
                        durationMillis = NEW_DURATION
                    )
                )
            }
            LaunchedEffect(this) {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        delayMillis = SWIPE_DURATION,
                        durationMillis = NEW_DURATION
                    )
                )
            }
        }
    }

    @Composable
    override fun BoardScope.RenderStaticTile(tileModel: TileModel) {
        key(tileModel.id) {
            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = tileModel.curPosition.row,
                        col = tileModel.curPosition.col,
                        layer = Layer.CellLayer
                    )
            )
        }
    }

    @Composable
    override fun BoardScope.RenderSwipedTile(tileModel: TileModel) {
        key(tileModel.id) {
            val row = remember { Animatable(tileModel.prevPosition!!.row) }
            val col = remember { Animatable(tileModel.prevPosition!!.col) }
            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = row.value,
                        col = col.value,
                        layer = Layer.AnimationLayer
                    )
            )
            if (row.value != tileModel.curPosition.row) {
                LaunchedEffect(this) {
                    row.animateTo(
                        targetValue = tileModel.curPosition.row,
                        animationSpec = tween(durationMillis = SWIPE_DURATION)
                    )
                }
            }
            if (col.value != tileModel.curPosition.col) {
                LaunchedEffect(this) {
                    col.animateTo(
                        targetValue = tileModel.curPosition.col,
                        animationSpec = tween(durationMillis = SWIPE_DURATION)
                    )
                }
            }
        }
    }

    @Composable
    override fun BoardScope.RenderMergedTile(tileModel: TileModel) {
        key(tileModel.id) {
            val scale = remember { Animatable(0f) }
            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = tileModel.curPosition.row,
                        col = tileModel.curPosition.col,
                        layer = Layer.MergeLayer
                    )
                    .scale(scale.value),
            )
            LaunchedEffect(this) {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = keyframes {
                        delayMillis = SWIPE_DURATION
                        durationMillis = MERGE_DURATION
                        0f atFraction 0f
                        1.2f atFraction 0.5f
                        1f atFraction 1f
                    }
                )
            }
        }
    }

    private fun Int.toBackgroundColor(): Color = 
        tileBackgroundColors[this] ?: Color(0xff3c3a32)

    private fun Int.toTextColor(): Color =
        if (this < 8) TileDarkText else TileLightText

    private fun Int.toFontSize() =
        when {
            this < 100 -> 32.sp
            this < 1000 -> 28.sp
            this >= 1000 -> 22.sp
            else -> 18.sp
        }
}
