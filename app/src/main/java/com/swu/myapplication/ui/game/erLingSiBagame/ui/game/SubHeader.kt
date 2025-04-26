package com.swu.myapplication.ui.game.erLingSiBagame.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onResetClicked,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Primary,
                contentColor = TileLightText
            )
        ) {
            Text(
                text = R.string.sub_header_new_game.resolve(),
                fontWeight = FontWeight.Bold
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
