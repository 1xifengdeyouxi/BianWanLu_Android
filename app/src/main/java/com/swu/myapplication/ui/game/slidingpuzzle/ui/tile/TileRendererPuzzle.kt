package com.swu.myapplication.ui.game.slidingpuzzle.ui.tile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 动画持续时间常量
private const val NEW_TILE_DURATION = 300
private const val MOVE_ANIMATION_DURATION = 200
private const val TILE_APPEAR_DELAY = 50 // 每个方块出现的延迟时间

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
    
    // 是否是新游戏状态
    private var isNewGame = true

    // 记录每个位置的动画状态
    private val positionAnimationMap = mutableStateMapOf<Int, Pair<Animatable<Float, AnimationVector1D>, Animatable<Float, AnimationVector1D>>>()

    // 将索引转换为行列位置
    private fun indexToPosition(index: Int): TilePosition {
        return TilePosition(
            row = index / puzzleSize,
            col = index % puzzleSize
        )
    }

    // 渲染单个拼图方块
    @Composable
    fun RenderTile(
        index: Int,
        value: Int,
        puzzleState: List<Int>,
        cellSize: Dp,
        spacing: Dp = 4.dp,
        isNewGame: Boolean = false
    ) {
        // 如果是空白方块(0)，不渲染
        if (value == 0) return
        
        val currentPosition = indexToPosition(index)
        val density = LocalDensity.current
        
        // 计算方块的序号，用于错开动画
        val tileOrder = currentPosition.row * puzzleSize + currentPosition.col
        
        // 每个单元格的实际大小（包含间距）
        val totalTileSize = cellSize + spacing
        
        // 计算方块在网格中的位置
        val offsetX = with(density) { (currentPosition.col * totalTileSize.toPx()) }
        val offsetY = with(density) { (currentPosition.row * totalTileSize.toPx()) }
        
        // 为新游戏状态创建动画
        if (isNewGame) {
            // 创建缩放和透明度动画
            val scale = remember { Animatable(0f) }
            val alpha = remember { Animatable(0f) }
            
            // 渲染方块
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { offsetX.toDp() },
                        y = with(density) { offsetY.toDp() }
                    )
                    .size(cellSize)
                    .scale(scale.value) // 应用缩放
                    .alpha(alpha.value) // 应用透明度
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4CAF50))
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
            
            // 启动动画
            LaunchedEffect(key1 = isNewGame, key2 = value) {
                // 添加延迟，使方块按顺序出现
                delay(tileOrder.toLong() * TILE_APPEAR_DELAY)
                
                // 同时启动缩放和透明度动画
                launch {
                    scale.animateTo(
                        targetValue = 1f, 
                        animationSpec = keyframes {
                            durationMillis = NEW_TILE_DURATION
                            0f at 0
                            1.1f at (NEW_TILE_DURATION * 0.6).toInt() // 稍微放大
                            1f at NEW_TILE_DURATION
                        }
                    )
                }
                
                launch {
                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = NEW_TILE_DURATION)
                    )
                }
            }
        } else {
            // 非新游戏状态，渲染普通方块（可能有移动动画）
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
    
            // 计算方块颜色
            val tileColor = Color(0xFF4CAF50)
            val animatedColor = tileColor.copy(alpha = 0.7f + (colorAnimation.value * 0.3f))
    
            // 计算方块在网格中的位置
            val animOffsetX = with(density) { (colAnim.value * totalTileSize.toPx()) }
            val animOffsetY = with(density) { (rowAnim.value * totalTileSize.toPx()) }
    
            // 渲染方块
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { animOffsetX.toDp() },
                        y = with(density) { animOffsetY.toDp() }
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
    }

    // 更新之前的拼图状态
    fun updatePreviousState(state: List<Int>) {
        previousPuzzleState = state.toList() // 创建一个副本以避免引用问题
    }
    
    // 设置是否是新游戏
    fun setNewGame(isNew: Boolean) {
        if (isNew) {
            // 清除位置动画状态
            positionAnimationMap.clear()
        }
        isNewGame = isNew
    }
}