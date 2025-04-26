package com.swu.myapplication.ui.game.erLingSiBagame.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swu.myapplication.R
import com.swu.myapplication.ui.game.erLingSiBagame.resolve
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Compose2048Theme
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Primary
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.TileLightText

@Composable
fun SubHeader(
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onResetClicked,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Primary,
                contentColor = TileLightText
            ),
            shape = RoundedCornerShape(6.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 1.dp
            ),
            modifier = Modifier
                .width(90.dp)
                .height(60.dp)
        ) {
            Text(
                text = R.string.sub_header_new_game.resolve(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
private fun SubHeaderPreview() {
    Compose2048Theme {
        SubHeader(onResetClicked = {})
    }
}
