package com.swu.myapplication.ui.game.slidingpuzzle.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.myapplication.ui.game.slidingpuzzle.PuzzleManager

class PuzzleScreenViewModel(
    private val puzzleManager: PuzzleManager
) : ViewModel() {
    
    // 棋盘尺寸
    var puzzleSize by mutableStateOf(puzzleManager.getCurrentSize())
        private set
    
    // 拼图状态
    var puzzleState by mutableStateOf(puzzleManager.getPuzzleState())
        private set
    
    // 移动步数
    var moves by mutableStateOf(puzzleManager.getMoveCount())
        private set
    
    // 最佳步数
    var bestMoves by mutableStateOf(puzzleManager.getBestMoveCount())
        private set
    
    // 是否完成
    var isCompleted by mutableStateOf(puzzleManager.isCompleted())
        private set
    
    // 点击方块
    fun onTileClicked(position: Int) {
        val moved = puzzleManager.moveTile(position)
        if (moved) {
            // 更新状态
            puzzleState = puzzleManager.getPuzzleState()
            moves = puzzleManager.getMoveCount()
            isCompleted = puzzleManager.isCompleted()
            
            if (isCompleted) {
                bestMoves = puzzleManager.getBestMoveCount()
            }
        }
    }
    
    // 重置拼图
    fun resetPuzzle() {
        puzzleManager.resetPuzzle()
        updateState()
    }
    
    // 修改拼图尺寸
    fun changePuzzleSize(size: Int) {
        puzzleManager.changePuzzleSize(size)
        puzzleSize = size
        updateState()
    }
    
    private fun updateState() {
        puzzleState = puzzleManager.getPuzzleState()
        moves = puzzleManager.getMoveCount()
        isCompleted = puzzleManager.isCompleted()
        bestMoves = puzzleManager.getBestMoveCount()
    }
}

class PuzzleScreenViewModelFactory(
    private val puzzleManager: PuzzleManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PuzzleScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PuzzleScreenViewModel(puzzleManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}