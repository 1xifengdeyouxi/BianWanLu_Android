package com.swu.myapplication.ui.game.slidingpuzzle

import android.content.Context
import android.content.SharedPreferences
import kotlin.random.Random

class PuzzleManager(
    private var size: Int,
    private val storageManager: PuzzleStorageManager
) {
    // 拼图当前状态
    private var puzzleState: MutableList<Int> = mutableListOf()
    
    // 空白块的位置索引
    private var emptyTileIndex: Int = 0
    
    // 是否已完成
    private var completed: Boolean = false
    
    // 移动步数
    private var moveCount: Int = 0
    
    init {
        initializePuzzle()
    }
    
    fun changePuzzleSize(newSize: Int) {
        size = newSize
        initializePuzzle()
    }
    
    fun getCurrentSize(): Int = size
    
    fun getPuzzleState(): List<Int> = puzzleState.toList()
    
    fun getMoveCount(): Int = moveCount
    
    fun getBestMoveCount(): Int = storageManager.getBestMoves(size)
    
    fun isCompleted(): Boolean = completed
    
    fun resetPuzzle() {
        initializePuzzle()
    }
    
    fun moveTile(position: Int): Boolean {
        if (completed || position < 0 || position >= size * size) {
            return false
        }
        
        // 检查位置是否与空白块相邻
        if (!isAdjacentToEmpty(position)) {
            return false
        }
        
        // 交换空白块和选中的块
        val temp = puzzleState[position]
        puzzleState[position] = 0
        puzzleState[emptyTileIndex] = temp
        emptyTileIndex = position
        
        // 增加移动步数
        moveCount++
        
        // 检查是否完成拼图
        checkCompletion()
        
        return true
    }
    
    private fun initializePuzzle() {
        // 重置状态
        moveCount = 0
        completed = false
        
        // 初始化有序数组 [1, 2, 3, ..., size*size-1, 0]
        puzzleState = MutableList(size * size) { it + 1 }
        puzzleState[size * size - 1] = 0
        emptyTileIndex = size * size - 1
        
        // 打乱拼图 (确保可解)
        do {
            shufflePuzzle()
        } while (!isSolvable() || isOrdered())
    }
    
    private fun shufflePuzzle() {
        // 随机交换，确保生成的拼图状态是可解的
        for (i in 0 until 1000) {
            val adjacentPositions = getAdjacentPositions(emptyTileIndex)
            if (adjacentPositions.isNotEmpty()) {
                val randomPosition = adjacentPositions[Random.nextInt(adjacentPositions.size)]
                val temp = puzzleState[randomPosition]
                puzzleState[randomPosition] = 0
                puzzleState[emptyTileIndex] = temp
                emptyTileIndex = randomPosition
            }
        }
    }
    
    private fun isAdjacentToEmpty(position: Int): Boolean {
        val row = position / size
        val col = position % size
        val emptyRow = emptyTileIndex / size
        val emptyCol = emptyTileIndex % size
        
        // 检查是否在同一行且列相邻，或者在同一列且行相邻
        return (row == emptyRow && kotlin.math.abs(col - emptyCol) == 1) ||
                (col == emptyCol && kotlin.math.abs(row - emptyRow) == 1)
    }
    
    private fun getAdjacentPositions(position: Int): List<Int> {
        val row = position / size
        val col = position % size
        val result = mutableListOf<Int>()
        
        // 上方
        if (row > 0) {
            result.add(position - size)
        }
        
        // 下方
        if (row < size - 1) {
            result.add(position + size)
        }
        
        // 左方
        if (col > 0) {
            result.add(position - 1)
        }
        
        // 右方
        if (col < size - 1) {
            result.add(position + 1)
        }
        
        return result
    }
    
    private fun isSolvable(): Boolean {
        // 检查拼图是否可解
        var inversions = 0
        for (i in puzzleState.indices) {
            if (puzzleState[i] == 0) continue
            
            for (j in i + 1 until puzzleState.size) {
                if (puzzleState[j] != 0 && puzzleState[i] > puzzleState[j]) {
                    inversions++
                }
            }
        }
        
        // 对于偶数大小的拼图，(inversions + emptyRow) 的奇偶性决定是否可解
        // 对于奇数大小的拼图，inversions 的奇偶性决定是否可解
        val emptyRow = emptyTileIndex / size
        
        return if (size % 2 == 1) {
            // 奇数尺寸棋盘
            inversions % 2 == 0
        } else {
            // 偶数尺寸棋盘
            (inversions + emptyRow) % 2 == 1
        }
    }
    
    private fun isOrdered(): Boolean {
        // 检查是否已经是有序状态
        for (i in 0 until size * size - 1) {
            if (puzzleState[i] != i + 1) {
                return false
            }
        }
        return puzzleState[size * size - 1] == 0
    }
    
    private fun checkCompletion() {
        if (isOrdered()) {
            completed = true
            
            // 保存最佳移动步数
            val bestMoves = storageManager.getBestMoves(size)
            if (moveCount < bestMoves) {
                storageManager.saveBestMoves(size, moveCount)
            }
        }
    }
}

interface PuzzleStorageManager {
    fun getBestMoves(size: Int): Int
    fun saveBestMoves(size: Int, moves: Int)
}

class PuzzleStorageManagerImpl(context: Context) : PuzzleStorageManager {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    override fun getBestMoves(size: Int): Int {
        return prefs.getInt(KEY_BEST_MOVES + size, Int.MAX_VALUE)
    }
    
    override fun saveBestMoves(size: Int, moves: Int) {
        prefs.edit().putInt(KEY_BEST_MOVES + size, moves).apply()
    }
    
    companion object {
        private const val PREFS_NAME = "sliding_puzzle_prefs"
        private const val KEY_BEST_MOVES = "best_moves_"
    }
}