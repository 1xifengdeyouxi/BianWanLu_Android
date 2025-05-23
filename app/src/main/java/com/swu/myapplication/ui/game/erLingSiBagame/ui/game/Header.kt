package com.swu.myapplication.ui.game.erLingSiBagame.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swu.myapplication.R
import com.swu.myapplication.ui.game.erLingSiBagame.resolve
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Compose2048Theme
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Primary
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.TileLightText

@Composable
fun Header(
    score: Int,
    bestScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        HeaderPanel(title = R.string.header_score.resolve(), value = score.toString())
        Spacer(modifier = Modifier.requiredWidth(8.dp))
        HeaderPanel(title = R.string.header_best.resolve(), value = bestScore.toString())
    }
}

@Composable
fun HeaderPanel(
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
            .background(Primary, RoundedCornerShape(8.dp))
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

@Preview
@Composable
private fun HeaderPreview() {
    Compose2048Theme {
        Header(score = 128, bestScore = 65536)
    }
}
