package com.swu.myapplication.ui.game.erLingSiBagame.ui.theme

import androidx.compose.ui.graphics.Color

// 主题颜色
val Primary = Color(0xFFedc22e)
val PrimaryVariant = Color(0xFFedc53f)
val Secondary = Color(0xFFeee4da)
val Background = Color(0xFFFAF8EF)
val OnSurface = Color(0xFF776E65)

// 游戏板颜色
val BoardBackground = Color(0xFFbbada0)
val EmptyCellColor = Color(0xFFcdc1b4)

// 数字砖块背景颜色映射
val tileBackgroundColors = mapOf(
    2 to Color(0xffeee4da),
    4 to Color(0xffede0c8),
    8 to Color(0xfff2b179),
    16 to Color(0xfff59563),
    32 to Color(0xfff67c5f),
    64 to Color(0xfff65e3b),
    128 to Color(0xffedcf72),
    256 to Color(0xffedcc61),
    512 to Color(0xffedc850),
    1024 to Color(0xffedc53f),
    2048 to Color(0xffedc22e)
)

// 砖块文字颜色
val TileDarkText = Color(0xFF776E65)  // 用于2和4的砖块
val TileLightText = Color(0xFFF9F6F2)  // 用于8及以上的砖块
