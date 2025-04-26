package com.swu.myapplication.ui.game.erLingSiBagame.ui.board

import androidx.compose.runtime.Composable
import com.swu.myapplication.ui.game.erLingSiBagame.ui.data.BoardModel
import com.swu.myapplication.ui.game.erLingSiBagame.ui.tile.TileRenderer
import com.swu.myapplication.ui.game.erLingSiBagame.ui.tile.TileRendererInstance

interface BoardRenderer {
    @Composable
    fun BoardScope.Render(boardModel: BoardModel)
}

internal object BoardRendererInstance :
    BoardRenderer,
    TileRenderer by TileRendererInstance {

    @Composable
    override fun BoardScope.Render(boardModel: BoardModel) {
        boardModel.staticTiles.forEach { tileModel -> RenderStaticTile(tileModel) }
        boardModel.swipedTiles.forEach { tileModel -> RenderSwipedTile(tileModel) }
        boardModel.mergedTiles.forEach { tileModel -> RenderMergedTile(tileModel) }
        boardModel.newTiles.forEach { tileModel -> RenderNewTile(tileModel) }
    }
}
