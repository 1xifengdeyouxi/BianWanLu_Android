package com.swu.myapplication.ui.game.slidingpuzzle.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swu.myapplication.R
import com.swu.myapplication.ui.game.erLingSiBagame.resolve
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.PrimaryColor
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.SlidingPuzzleTheme
import com.swu.myapplication.ui.game.slidingpuzzle.ui.theme.TileLightText

@Composable
fun PuzzleSubHeader(
    onResetClicked: () -> Unit,
    selectDifficultyClicked: () -> Unit = onResetClicked,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        // 新游戏按钮
        Button(
            onClick = onResetClicked,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = PrimaryColor,
                contentColor = TileLightText
            ),
            shape = RoundedCornerShape(6.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 1.dp
            ),
            modifier = Modifier
                .width(90.dp)
                .height(28.dp)
        ) {
            Text(
                text = R.string.sliding_puzzle_new_game.resolve(),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        
        // 间距
        Spacer(modifier = Modifier.height(4.dp))
        
        // 选择难度按钮
        Button(
            onClick = selectDifficultyClicked,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = PrimaryColor,
                contentColor = TileLightText
            ),
            shape = RoundedCornerShape(6.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 1.dp
            ),
            modifier = Modifier
                .width(90.dp)
                .height(28.dp)
        ) {
            Text(
                text = R.string.sliding_puzzle_select_difficulty.resolve(),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
private fun PuzzleSubHeaderPreview() {
    SlidingPuzzleTheme {
        PuzzleSubHeader(onResetClicked = {})
    }
}