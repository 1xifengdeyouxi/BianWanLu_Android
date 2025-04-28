package com.swu.myapplication.ui.game.slidingpuzzle.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swu.myapplication.R
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.PrimaryColor
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.SlidingPuzzleTheme
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.TileLightText

@Composable
fun PuzzleHeader(
    moves: Int,
    bestMoves: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        PuzzleHeaderPanel(
            title = stringResource(R.string.sliding_puzzle_header_moves),
            value = moves.toString()
        )
        Spacer(modifier = Modifier.requiredWidth(8.dp))
        PuzzleHeaderPanel(
            title = stringResource(R.string.sliding_puzzle_header_best),
            value = if (bestMoves < Int.MAX_VALUE) bestMoves.toString() else "-"
        )
    }
}

@Composable
fun PuzzleHeaderPanel(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentWidth()
            .requiredHeight(60.dp)
            .requiredWidth(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryColor, RoundedCornerShape(8.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = TileLightText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TileLightText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        )
    }
}

@Composable
fun stringResource(id: Int): String {
    val context = LocalContext.current
    return context.getString(id)
}

@Preview
@Composable
private fun PuzzleHeaderPreview() {
    SlidingPuzzleTheme {
        PuzzleHeader(moves = 42, bestMoves = 38)
    }
}