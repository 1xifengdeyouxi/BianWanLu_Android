package com.swu.myapplication.ui.game.erLingSiBagame.ui.tile

// 导入Jetpack Compose动画相关库
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
// 导入Compose运行时和UI组件
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
// 导入图形效果修饰符
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
// 导入自定义组件和主题
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.BoardScope
import com.swu.myapplication.ui.game.erLingSiBagame.ui.board.Layer
import com.swu.myapplication.ui.game.erLingSiBagame.ui.data.TileModel
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.TileDarkText
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.TileLightText
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.tileBackgroundColors

// 定义动画持续时间常量（单位：毫秒）
private const val NEW_DURATION = 150    // 新方块出现动画时长
private const val SWIPE_DURATION = 120  // 滑动动画时长
private const val MERGE_DURATION = 200  // 合并动画时长

// 定义方块渲染器接口
interface TileRenderer {
    // 声明四个不同状态的渲染方法
    @Composable
    fun BoardScope.RenderNewTile(tileModel: TileModel)    // 渲染新生成的方块

    @Composable
    fun BoardScope.RenderStaticTile(tileModel: TileModel) // 渲染静止状态的方块

    @Composable
    fun BoardScope.RenderSwipedTile(tileModel: TileModel) // 渲染滑动中的方块

    @Composable
    fun BoardScope.RenderMergedTile(tileModel: TileModel) // 渲染合并时的方块
}

// 实现TileRenderer接口的单例对象
object TileRendererInstance : TileRenderer {

    // 渲染新生成的方块（带缩放和淡入效果）
    @Composable
    override fun BoardScope.RenderNewTile(tileModel: TileModel) {
        key(tileModel.id) {  // 根据id标识组件，优化重组
            // 创建动画状态（缩放从0开始，透明度从0开始）
            val scale = remember { Animatable(0f) }
            val alpha = remember { Animatable(0f) }

            // 调用基础Tile组件
            Tile(
                fraction = tileFraction,  // 方块尺寸比例
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(  // 自定义扩展函数，设置位置和层级
                        row = tileModel.curPosition.row,
                        col = tileModel.curPosition.col,
                        layer = Layer.AnimationLayer  // 放在动画层
                    )
                    .scale(scale.value)  // 应用缩放
                    .alpha(alpha.value)  // 应用透明度
            )

            // 启动缩放动画
            LaunchedEffect(this) {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        delayMillis = SWIPE_DURATION,  // 延迟等待滑动动画完成
                        durationMillis = NEW_DURATION
                    )
                )
            }

            // 启动淡入动画
            LaunchedEffect(this) {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        delayMillis = SWIPE_DURATION,
                        durationMillis = NEW_DURATION
                    )
                )
            }
        }
    }

    // 渲染静态方块（无动画）
    @Composable
    override fun BoardScope.RenderStaticTile(tileModel: TileModel) {
        key(tileModel.id) {
            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = tileModel.curPosition.row,
                        col = tileModel.curPosition.col,
                        layer = Layer.CellLayer  // 放在基础层
                    )
            )
        }
    }

    // 渲染滑动中的方块（位置移动动画）
    @Composable
    override fun BoardScope.RenderSwipedTile(tileModel: TileModel) {
        key(tileModel.id) {
            // 记录起始位置（从上一个位置开始动画）
            val row = remember { Animatable(tileModel.prevPosition!!.row) }
            val col = remember { Animatable(tileModel.prevPosition!!.col) }

            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = row.value,  // 使用动画值
                        col = col.value,
                        layer = Layer.AnimationLayer
                    )
            )

            // 行坐标动画
            if (row.value != tileModel.curPosition.row) {
                LaunchedEffect(this) {
                    row.animateTo(
                        targetValue = tileModel.curPosition.row,
                        animationSpec = tween(durationMillis = SWIPE_DURATION)
                    )
                }
            }

            // 列坐标动画
            if (col.value != tileModel.curPosition.col) {
                LaunchedEffect(this) {
                    col.animateTo(
                        targetValue = tileModel.curPosition.col,
                        animationSpec = tween(durationMillis = SWIPE_DURATION)
                    )
                }
            }
        }
    }

    // 渲染合并时的方块（弹跳缩放动画）
    @Composable
    override fun BoardScope.RenderMergedTile(tileModel: TileModel) {
        key(tileModel.id) {
            val scale = remember { Animatable(0f) }  // 缩放动画状态

            Tile(
                fraction = tileFraction,
                value = tileModel.curValue.toString(),
                color = tileModel.curValue.toTextColor(),
                fontSize = tileModel.curValue.toFontSize(),
                backgroundColor = tileModel.curValue.toBackgroundColor(),
                modifier = Modifier
                    .boardCell(
                        row = tileModel.curPosition.row,
                        col = tileModel.curPosition.col,
                        layer = Layer.MergeLayer  // 放在合并层
                    )
                    .scale(scale.value),  // 应用缩放
            )

            // 关键帧动画（先放大再缩小）
            LaunchedEffect(this) {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = keyframes {
                        delayMillis = SWIPE_DURATION      // 延迟等待滑动完成
                        durationMillis = MERGE_DURATION  // 总动画时长
                        0f atFraction 0f     // 起始值
                        1.2f atFraction 0.5f // 中间放大到120%
                        1f atFraction 1f     // 最终恢复原大小
                    }
                )
            }
        }
    }

    // 扩展函数：根据数值获取背景色
    private fun Int.toBackgroundColor(): Color =
        tileBackgroundColors[this] ?: Color(0xff3c3a32)  // 默认颜色

    // 扩展函数：根据数值获取文字颜色（深色/浅色）
    private fun Int.toTextColor(): Color =
        if (this < 8) TileDarkText else TileLightText  // 数值小时用深色，大时用浅色

    // 扩展函数：根据数值动态调整字体大小
    private fun Int.toFontSize() =
        when {
            this < 100 -> 32.sp    // 两位数：32sp
            this < 1000 -> 28.sp   // 三位数：28sp
            this >= 1000 -> 22.sp  // 四位数：22sp
            else -> 18.sp         // 默认值
        }
}