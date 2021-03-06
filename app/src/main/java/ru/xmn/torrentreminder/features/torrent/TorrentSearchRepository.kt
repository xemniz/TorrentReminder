package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable

interface TorrentSearchRepository {
    fun delete(id: String)
    fun subscribeSearch(id: String): Flowable<TorrentSearch>
    fun subscribeAllSearches(): Flowable<List<TorrentSearch>>
    fun insert(searchQuery: String, dataList: List<TorrentData>)
    fun update(id: String, searchQuery: String, dataList: List<TorrentData>)
    fun checkAllItemsInSearchAsViewed(id: String)
}