package com.swu.myapplication.ui.game.slidingpuzzle.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swu.myapplication.R
import com.swu.myapplication.ui.game.erLingSiBagame.resolve
import com.swu.myapplication.ui.game.slidingpuzzle.PuzzleManager
import com.swu.myapplication.ui.game.slidingpuzzle.PuzzleStorageManagerImpl
import com.swu.myapplication.ui.game.slidingpuzzle.ui.board.PuzzleBoard
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.Background
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.OnSurface
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.PrimaryColor
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.SlidingPuzzleTheme
import kotlinx.coroutines.delay


@Composable
fun PuzzleScreen(
    modifier: Modifier = Modifier,
    size: Int = 4,
    viewModel: PuzzleScreenViewModel = viewModel(
        factory = PuzzleScreenViewModelFactory(
            PuzzleManager(
                size,
                PuzzleStorageManagerImpl(LocalContext.current)
            )
        )
    )
) {
    // 添加动画过渡效果
    val visible = remember { mutableStateOf(false) }
    val showDifficultyDialog = remember { mutableStateOf(false) }
    // 添加新游戏状态
    val isNewGame = remember { mutableStateOf(true) } // 初始为true，显示初始动画
    
    LaunchedEffect(key1 = Unit) {
        visible.value = true
    }

    // 重置游戏的处理函数
    val handleResetGame = {
        isNewGame.value = true
        viewModel.resetPuzzle()
    }
    
    // 在动画完成后关闭newGame状态
    LaunchedEffect(isNewGame.value) {
        if (isNewGame.value) {
            // 等待足够的时间让所有动画完成
            delay((size * size * 50 + 500).toLong()) // 基于方块数量和延迟计算
            isNewGame.value = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 顶部行：将Header和SubHeader放到同一行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 得分面板
                    PuzzleHeader(
                        moves = viewModel.moves,
                        bestMoves = viewModel.bestMoves,
                        modifier = Modifier.weight(3f)
                    )

                    // 操作按钮
                    PuzzleSubHeader(
                        onResetClicked = { handleResetGame() },
                        selectDifficultyClicked = { showDifficultyDialog.value = true },
                        modifier = Modifier.weight(2f)
                    )
                }

                // 游戏操作提示
                Text(
                    text = R.string.sliding_puzzle_tip.resolve(),
                    style = MaterialTheme.typography.subtitle2,
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 游戏主板
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(1f)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            clip = true
                        )
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    PuzzleBoard(
                        puzzleSize = viewModel.puzzleSize,
                        puzzleState = viewModel.puzzleState,
                        onTileClicked = { position -> 
                            // 只有在动画完成后才允许点击
                            if (!isNewGame.value) {
                                viewModel.onTileClicked(position) 
                            }
                        },
                        isCompleted = viewModel.isCompleted,
                        isNewGame = isNewGame.value,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 游戏难度
                Text(
                    text = when (viewModel.puzzleSize) {
                        3 -> R.string.sliding_puzzle_easy.resolve()
                        4 -> R.string.sliding_puzzle_medium.resolve()
                        else -> R.string.sliding_puzzle_hard.resolve()
                    },
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                )
            }
        }
        
        // 难度选择对话框
        if (showDifficultyDialog.value) {
            AlertDialog(
                onDismissRequest = { showDifficultyDialog.value = false },
                title = { Text(text = R.string.sliding_puzzle_select_difficulty.resolve()) },
                buttons = {
                    Column(
                        modifier = Modifier.padding(all = 16.dp)
                    ) {
                        Button(
                            onClick = { 
                                showDifficultyDialog.value = false
                                isNewGame.value = true
                                viewModel.changePuzzleSize(3)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor)
                        ) {
                            Text(text = R.string.sliding_puzzle_easy.resolve(), color = Color.White)
                        }
                        
                        Button(
                            onClick = { 
                                showDifficultyDialog.value = false
                                isNewGame.value = true
                                viewModel.changePuzzleSize(4)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor)
                        ) {
                            Text(text = R.string.sliding_puzzle_medium.resolve(), color = Color.White)
                        }
                        
                        Button(
                            onClick = { 
                                showDifficultyDialog.value = false
                                isNewGame.value = true
                                viewModel.changePuzzleSize(5)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor)
                        ) {
                            Text(text = R.string.sliding_puzzle_hard.resolve(), color = Color.White)
                        }
                    }
                }
            )
        }
        
        // 完成游戏提示
        if (viewModel.isCompleted) {
            AlertDialog(
                onDismissRequest = { handleResetGame() },
                title = { 
                    Text(
                        text = R.string.sliding_puzzle_message_win.resolve(),
                        style = MaterialTheme.typography.h5
                    ) 
                },
                text = { 
                    Text(
                        text = "你用了 ${viewModel.moves} 步完成了拼图！",
                        style = MaterialTheme.typography.body1
                    ) 
                },
                confirmButton = {
                    Button(
                        onClick = { handleResetGame() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor)
                    ) {
                        Text(
                            text = R.string.sliding_puzzle_new_game.resolve(),
                            color = Color.White
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun PuzzleScreenPreview() {
    SlidingPuzzleTheme {
        PuzzleScreen()
    }
}