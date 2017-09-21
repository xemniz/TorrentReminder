package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable

interface TorrentSearchRepository {
    fun delete(searchQuery: String)
    fun subscribeSearch(searchQuery: String): Flowable<TorrentSearch>
    fun subscribeAllSearches(): Flowable<List<TorrentSearch>>
    fun insertOrUpdate(searchQuery: String, dataList: List<TorrentData>)
    fun checkAllAsViewed(searchQuery: String)
}