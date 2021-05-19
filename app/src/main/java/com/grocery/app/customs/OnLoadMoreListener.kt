package com.grocery.app.customs

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class OnLoadMoreListener : RecyclerView.OnScrollListener() {

    companion object {
        private const val THRESHOLD_ITEM = 1
    }

    abstract val hasMore: Boolean
    abstract val isRequesting: Boolean
    abstract fun onLoadMore()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layManager = recyclerView.layoutManager
        if (layManager !is LinearLayoutManager)
            return
        if (layManager.itemCount > 0 && !isRequesting && hasMore && reachedToEnd(layManager)) {
            onLoadMore()
        }
    }

    private fun reachedToEnd(layoutManager: LinearLayoutManager): Boolean {
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        return lastVisiblePosition + THRESHOLD_ITEM + 1 >= layoutManager.itemCount
    }
}