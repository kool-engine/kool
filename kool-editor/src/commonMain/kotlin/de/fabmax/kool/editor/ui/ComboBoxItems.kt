package de.fabmax.kool.editor.ui

class ComboBoxItems<T>(items: List<T>, labelMapping: (T) -> String = { "$it" }) {

    val options = items.map { Item(it, labelMapping(it)) }
    val optionsAndNone = buildList<Item<T>> {
        add(Item(null, ""))
        addAll(items.map { Item(it, labelMapping(it)) })
    }

    fun getOptionsAndIndex(selected: List<T>): Pair<List<Item<T>>, Int> {
        return if (selected.all { it == selected[0] }) {
            options to options.indexOfFirst { it.item == selected[0] }
        } else {
            optionsAndNone to 0
        }
    }

    class Item<T>(val item: T?, val label: String) {
        override fun toString(): String = label
    }
}