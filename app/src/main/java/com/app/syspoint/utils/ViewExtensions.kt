package com.app.syspoint.utils

import android.view.View
import androidx.appcompat.widget.SearchView

infix fun View.click(click: () -> Unit) {
    setOnClickListener { click() }
}

infix fun View.longClick(click: () -> Boolean) {
    setOnLongClickListener { click() }
}

infix fun SearchView.onFocusChange(onFocusChange: (view: View, hasFocus: Boolean) -> Unit) {
    setOnQueryTextFocusChangeListener { view, hasFocus -> run { onFocusChange(view, hasFocus) } }
}

fun SearchView.onQueryText(queryTextSubmit: (arg: String?) -> Unit, queryTextChange: (arg: String?) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            queryTextSubmit(query)
            return false;
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            queryTextChange(newText)
            return false;
        }
    })
}

fun View.setVisible(): View {
    this.visibility = View.VISIBLE
    return this
}

fun View.setInvisible(): View {
    this.visibility = View.INVISIBLE
    return this
}

fun View.setGone(): View {
    this.visibility = View.GONE
    return this
}