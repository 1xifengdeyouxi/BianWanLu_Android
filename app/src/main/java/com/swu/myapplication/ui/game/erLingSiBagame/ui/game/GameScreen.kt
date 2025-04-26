package com.swu.myapplication.ui.game.erLingSiBagame.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swu.myapplication.ui.game.erLingSiBagame.GameManager
import com.swu.myapplication.ui.game.erLingSiBagame.StorageManagerImpl
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.Board
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.BoardRendererInstance
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Background
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Compose2048Theme

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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                score = viewModel.score,
                bestScore = viewModel.bestScore,
            )
            SubHeader(
                onResetClicked = { viewModel.restartGame() }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 16.dp)
            ) {
                Board(
                    maxRows = size,
                    maxCols = size,
                    onTryAgainClicked = { viewModel.restartGame() },
                    modifier = Modifier
                        .fillMaxWidth()
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
