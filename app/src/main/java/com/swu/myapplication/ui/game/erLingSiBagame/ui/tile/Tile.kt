package com.swu.myapplication.ui.game.erLingSiBagame.ui.tile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

// 声明一个可组合函数，用于创建游戏中的数字方块
@Composable
fun Tile(
    fraction: Float,      // 方块在父容器中占据的比例（0-1）
    value: String,        // 方块显示的数字值
    color: Color,         // 文字颜色
    fontSize: TextUnit,   // 文字大小单位
    backgroundColor: Color, // 方块背景颜色
    modifier: Modifier = Modifier, // 可选的修饰符参数
) {
    // 使用Box作为方块容器
    Box(
        modifier = modifier
            .fillMaxSize(fraction)  // 设置占父容器的比例
            .padding(tilePadding.dp) // 添加内边距（4dp）
            .clip(RoundedCornerShape(tileCornerRadius.dp)) // 裁剪圆角形状（6dp半径）
            .shadow(2.dp, RoundedCornerShape(tileCornerRadius.dp)) // 添加阴影效果
            .background(backgroundColor, RoundedCornerShape(tileCornerRadius.dp)), // 设置背景颜色和形状
        contentAlignment = Alignment.Center, // 内容居中显示
    ) {
        // 显示数字的文本组件
        Text(
            text = value,         // 显示传入的数字值
            fontSize = fontSize,   // 设置文字大小
            fontWeight = FontWeight.Bold, // 文字加粗
            color = color,         // 文字颜色
        )
    }
}

// 定义私有常量（仅在当前文件可见）
private const val tilePadding = 4      // 方块的内部间距值（单位：dp）
private const val tileCornerRadius = 6 // 方块的圆角半径值（单位：dp）