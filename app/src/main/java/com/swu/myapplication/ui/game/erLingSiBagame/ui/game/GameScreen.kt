package com.swu.myapplication.ui.game.erLingSiBagame.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swu.myapplication.ui.game.erLingSiBagame.GameManager
import com.swu.myapplication.ui.game.erLingSiBagame.StorageManagerImpl
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.Board
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.BoardRendererInstance
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Background
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Compose2048Theme
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.OnSurface

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    size: Int = 4,
    viewModel: GameScreenViewModel = viewModel(
        factory = GameScreenViewModelFactory(
            GameManager(
                size,
                StorageManagerImpl(LocalContext.current)
            )
        )
    ),
) {
    // 添加动画过渡效果
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        visible.value = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
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
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 得分面板
                    Header(
                        score = viewModel.score,
                        bestScore = viewModel.bestScore,
                        modifier = Modifier.weight(3f)
                    )

                    // 操作按钮
                    SubHeader(
                        onResetClicked = { viewModel.restartGame() },
                        modifier = Modifier.weight(2f)
                    )
                }

                // 游戏操作提示
                Text(
                    text = "向上、下、左、右滑动合并相同数字",
                    style = MaterialTheme.typography.subtitle2,
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 游戏主板
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp),
                            clip = true
                        )
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Board(
                        maxRows = size,
                        maxCols = size,
                        onTryAgainClicked = { viewModel.restartGame() },
                        modifier = Modifier
                            .fillMaxSize()
                            .dragDetector(
                                enabled = !viewModel.won && !viewModel.over,
                                dragOffset = rememberDragOffset(),
                                onDragFinished = { dragOffset -> viewModel.applyDragGesture(dragOffset) }
                            ),
                        won = viewModel.won,
                        over = viewModel.over,
                    ) {
                        BoardRendererInstance.apply {
                            Render(viewModel.boardModel)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 游戏说明
                Text(
                    text = "达到2048的方块获胜!",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

internal fun Modifier.dragDetector(
    enabled: Boolean,
    dragOffset: MutableState<Offset>,
    onDragFinished: (Offset) -> Unit,
) = pointerInput(Unit) {
    if (enabled) {
        detectDragGestures(
            onDragStart = { dragOffset.value = Offset(0f, 0f) },
            onDragEnd = { onDragFinished(dragOffset.value) }
        ) { change, dragAmount ->
            change.consume()
            dragOffset.value += Offset(dragAmount.x, dragAmount.y)
        }
    }
}

@Composable
internal fun rememberDragOffset() = remember { mutableStateOf(Offset(0f, 0f)) }

@Preview
@Composable
private fun GamePreview() {
    Compose2048Theme {
        GameScreen()
    }
}
