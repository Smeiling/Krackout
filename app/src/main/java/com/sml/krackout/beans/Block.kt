package com.sml.krackout.beans

import android.graphics.RectF

/**
 * 方块类Bean
 * Created by Smeiling on 2017/12/25.
 */
class Block {
    /**
     * 方块颜色
     */
    lateinit var color: String

    /**
     * 方块坐标
     */
    lateinit var rectF: RectF

    /**
     * 是否已碰撞到
     * false：未碰撞，true：已碰撞
     */
    var isImpact = false

}