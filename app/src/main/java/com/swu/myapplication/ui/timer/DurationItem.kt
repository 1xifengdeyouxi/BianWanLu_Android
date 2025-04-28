package com.swu.myapplication.ui.timer

import java.io.Serializable

/**
 * 时长选项数据类
 * @param minutes 时长分钟数
 * @param displayText 显示的文本
 */
data class DurationItem(
    val minutes: Int,
    val displayText: String
) : Serializable 