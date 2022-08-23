package com.app.syspoint.utils

import android.view.View

class ViewExtensions {
}

infix fun View.click(click: () -> Unit) {
    setOnClickListener { click() }
}