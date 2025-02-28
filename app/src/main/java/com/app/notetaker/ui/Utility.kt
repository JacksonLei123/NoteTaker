package com.app.notetaker.ui

fun getTitleSubtitle(notes: String): Array<String> {
    val titles = arrayOf("New Note", "No additional text")
    val lines = notes.split("\n")
    var subIndex = 0
    for (i in lines.indices) {
        val title = lines[i].trim()
        if (title.isNotEmpty()) {
            titles[0] = if (title.length > 30) title.take(30) + "..." else title.take(20)
            subIndex = i
            break
        }
    }
    for (j in subIndex + 1 until lines.size) {
        val subtitle = lines[j].trim()
        if (subtitle.isNotEmpty()) {
            titles[1] = if (subtitle.length > 30) subtitle.take(30) + "..." else subtitle.take(20)
            return titles
        }
    }
    return titles

}
