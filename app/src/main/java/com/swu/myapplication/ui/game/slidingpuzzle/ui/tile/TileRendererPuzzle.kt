package com.swu.myapplication.ui.game.slidingpuzzle.ui.tile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.EmptyTileColor
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.TileColor
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.TileText
import kotlinx.coroutines.launch

// 动画持续时间常量
private const val MOVE_ANIMATION_DURATION = 200

/**
 * 表示方块的位置
 */
data class TilePosition(
    val row: Int,
    val col: Int
)

/**
 * 瓦片渲染器，负责渲染拼图方块
 */
class TileRendererPuzzle(
    private val puzzleSize: Int,
    private val onTileClicked: (Int) -> Unit,
    private val isCompleted: Boolean
) {
    // 保存上一个拼图状态，用于计算方块移动
    private var previousPuzzleState: List<Int>? = null

    // 记录每个位置的动画状态
    private val positionAnimationMap = mutableStateMapOf<Int, Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>>()

    // 将索引转换为行列位置
    private fun indexToPosition(index: Int): TilePosition {
        return TilePosition(
            row = index / puzzleSize,
            col = index % puzzleSize
        )
    }
    
    // 将值转换为应该在的位置索引
    private fun valueToTargetIndex(value: Int): Int {
        // 数值为0的空白方块特殊处理
        if (value == 0) return puzzleSize * puzzleSize - 1
        // 其他方块按照值-1的位置排列
        return value - 1
    }

    // 渲染单个拼图方块
    @Composable
    fun RenderTile(
        index: Int,
        value: Int,
        puzzleState: List<Int>,
        cellSize: Dp,
        spacing: Dp = 4.dp
    ) {
        val isEmptyTile = value == 0
        val density = LocalDensity.current
        
        // 如果是空白方块，不渲染
        if (isEmptyTile) return
        
        // 获取当前方块在拼图中的位置索引
        val currentPosition = indexToPosition(index)
        
        // 为每个方块创建唯一的动画状态
        val positionAnimations = remember(value) {
            positionAnimationMap.getOrPut(value) {
                Pair(
                    Animatable(currentPosition.row.toFloat()),
                    Animatable(currentPosition.col.toFloat())
                )
            }
        }

        val rowAnim = positionAnimations.first
        val colAnim = positionAnimations.second

        // 颜色动画
        val colorAnimation = remember { Animatable(0f) }

        // 计算方块是否移动了
        val hasMoved = remember(puzzleState) {
            val previous = previousPuzzleState
            if (previous != null && previous != puzzleState) {
                val prevIndex = previous.indexOf(value)
                prevIndex != index
            } else {
                false
            }
        }

        // 当拼图状态变化时，更新动画
        LaunchedEffect(puzzleState) {
            // 更新颜色动画
            colorAnimation.snapTo(0f)
            colorAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(300)
            )

            // 如果方块移动了，动画到新位置
            if (hasMoved) {
                launch {
                    rowAnim.animateTo(
                        targetValue = currentPosition.row.toFloat(),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }

                launch {
                    colAnim.animateTo(
                        targetValue = currentPosition.col.toFloat(),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            } else {
                // 如果没移动或是初始状态，立即设置到当前位置
                rowAnim.snapTo(currentPosition.row.toFloat())
                colAnim.snapTo(currentPosition.col.toFloat())
            }
        }

        // 计算方块颜色 - 使用鲜明的绿色
        val tileColor = Color(0xFF4CAF50)
        val animatedColor = tileColor.copy(alpha = 0.7f + (colorAnimation.value * 0.3f))

        // 每个单元格的实际大小（包含间距）
        val totalTileSize = cellSize + spacing
        
        // 计算方块在网格中的位置
        val offsetX = with(density) { (currentPosition.col * totalTileSize.toPx()) }
        val offsetY = with(density) { (currentPosition.row * totalTileSize.toPx()) }

        // 渲染方块
        Box(
            modifier = Modifier
                .offset(
                    x = with(density) { offsetX.toDp() },
                    y = with(density) { offsetY.toDp() }
                )
                .size(cellSize)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(animatedColor)
                .border(
                    width = 1.5.dp,
                    color = Color(0xFF2E7D32),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = !isCompleted) {
                    onTileClicked(index)
                },
            contentAlignment = Alignment.Center
        ) {
            // 显示方块数字
            Text(
                text = value.toString(),
                color = Color.White,
                fontSize = when (puzzleSize) {
                    3 -> 30.sp
                    4 -> 24.sp
                    else -> 18.sp
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

    // 更新之前的拼图状态
    fun updatePreviousState(state: List<Int>) {
        previousPuzzleState = state.toList() // 创建一个副本以避免引用问题
    }
}