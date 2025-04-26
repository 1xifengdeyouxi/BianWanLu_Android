package com.swu.myapplication.ui.game.erLingSiBagame.ui.game

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderTitle()
        HeaderPanel(title = R.string.header_score.resolve(), value = score.toString())
        Spacer(modifier = Modifier.requiredWidth(headerSpacing.dp))
        HeaderPanel(title = R.string.header_best.resolve(), value = bestScore.toString())
    }
}

@Composable
fun RowScope.HeaderTitle(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.weight(1f),
        text = R.string.app_name_short.resolve(),
        style = MaterialTheme.typography.h3,
        fontWeight = FontWeight.ExtraBold,
        color = Primary
    )
}

@Composable
fun HeaderPanel(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentWidth(unbounded = true)
            .requiredHeight(panelHeight.dp)
            .shadow(2.dp, RoundedCornerShape(panelCornerRadius.dp))
            .clip(RoundedCornerShape(panelCornerRadius.dp))
            .background(Primary, RoundedCornerShape(panelCornerRadius.dp))
            .padding(horizontal = panelPadding.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.body2,
            color = TileLightText,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            color = TileLightText,
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

private const val headerSpacing = 8
private const val panelHeight = 56
private const val panelPadding = 12
private const val panelCornerRadius = 6
