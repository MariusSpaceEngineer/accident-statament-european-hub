package com.inetum.realdolmen.crashkit.accidentsketch

import android.graphics.Canvas

interface IAccidentDrawable {
    val resId: Int
    val priority: Int
    fun draw(canvas: Canvas)
}