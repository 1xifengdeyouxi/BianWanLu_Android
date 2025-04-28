package com.swu.myapplication.ui.game.slidingpuzzle.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.SlidingPuzzleTheme
import com.swu.myapplication.ui.game.slidingpuzzle.ui.tile.TileRendererPuzzle

@Composable
fun PuzzleBoard(
    puzzleSize: Int,
    puzzleState: List<Int>,
    onTileClicked: (Int) -> Unit,
    isCompleted: Boolean,
    isNewGame: Boolean = false,
    modifier: Modifier = Modifier
) {
    // 创建瓦片渲染器
    val tileRenderer = remember(puzzleSize, onTileClicked, isCompleted) {
        TileRendererPuzzle(puzzleSize, onTileClicked, isCompleted)
    }
    
    // 网格单元格间距
    val gridSpacing = 4.dp
    
    // 获取屏幕宽度来计算单元格大小
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // 计算单元格大小
    val cellSize = remember(puzzleSize, screenWidth) {
        // 计算可用宽度，考虑屏幕宽度限制
        val maxBoardWidth = 360.dp
        val availableWidth = minOf(screenWidth * 0.85f, maxBoardWidth)
        
        // 计算每个单元格的大小，考虑间距
        val totalSpacing = gridSpacing * (puzzleSize - 1)
        val cellWidth = (availableWidth - totalSpacing) / puzzleSize
        
        cellWidth
    }
    
    // 设置新游戏状态
    LaunchedEffect(isNewGame) {
        tileRenderer.setNewGame(isNewGame)
    }
    
    // 当拼图状态变化时，更新渲染器
    LaunchedEffect(puzzleState) {
        tileRenderer.updatePreviousState(puzzleState)
    }
    
    // 主容器
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 棋盘容器
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .background(Color(0xFFECEFF1), RoundedCornerShape(12.dp))
                .padding(gridSpacing),
            contentAlignment = Alignment.TopStart // 确保从左上角开始布局
        ) {
            // 背景网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(puzzleSize),
                verticalArrangement = Arrangement.spacedBy(gridSpacing),
                horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize()
            ) {
                items(puzzleSize * puzzleSize) { index ->
                    // 背景格子
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color(0xFFCFD8DC), RoundedCornerShape(6.dp))
                    )
                }
            }
            
            // 渲染所有非空方块
            puzzleState.forEachIndexed { index, value ->
                if (value > 0) {  // 只渲染非空方块
                    tileRenderer.RenderTile(
                        index = index,
                        value = value,
                        puzzleState = puzzleState,
                        cellSize = cellSize,
                        spacing = gridSpacing,
                        isNewGame = isNewGame
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PuzzleBoardPreview() {
    SlidingPuzzleTheme {
        val puzzleState = listOf(1, 2, 3, 4, 5, 6, 7, 8, 0) // 3x3 puzzle
        PuzzleBoard(
            puzzleSize = 3,
            puzzleState = puzzleState,
            onTileClicked = {},
            isCompleted = false
        )
    }
}