package com.sml.krackout.utils

import android.content.Context

/**
 * Created by Smeiling on 2017/12/25.
 */
object ViewUtil {

    fun dpi2px(context: Context, dpi: Float): Int {
        return (context.resources.displayMetrics.density * dpi + 0.5f).toInt()
    }

}