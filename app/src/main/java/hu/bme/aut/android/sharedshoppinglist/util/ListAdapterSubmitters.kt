package hu.bme.aut.android.sharedshoppinglist.util

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.submitRemoveAt(
    items: List<T>,
    index: Int
): MutableList<T> {
    val tmpList = items.toMutableList()
    tmpList.removeAt(index)
    submitList(tmpList)
    return tmpList
}

fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.submitUpdateAt(
    items: List<T>,
    item: T,
    index: Int
): MutableList<T> {
    val tmpList = items.toMutableList()
    tmpList[index] = item
    submitList(tmpList)
    return tmpList
}

fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.submitAdd(
    items: List<T>,
    item: T,
    index: Int
): MutableList<T> {
    val tmpList = items.toMutableList()
    tmpList.add(index, item)
    submitList(tmpList)
    return tmpList
}