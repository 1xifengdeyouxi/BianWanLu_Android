package com.swu.myapplication.ui.timer

import java.io.Serializable

/**
 * 氛围项数据类
 * @param id 唯一标识
 * @param title 氛围名称
 * @param imageResId 氛围图标资源ID
 * @param soundResId 氛围声音资源ID
 * @param customImageUri 自定义图片URI
 */
data class AtmosphereItem(
    val id: Int,
    val title: String,
    val imageResId: Int,
    val soundResId: Int? = null,
    val customImageUri: String? = null
) : Serializable 