package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class TorrentSearchUseCase(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    fun search(searchQuery: String) {
        Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .subscribeOn(Schedulers.io())
                .subscribe { torrentSearchRepository.insertOrUpdate(searchQuery, it) }
    }

    fun checkAllAsViewed(searchQuery: String) {
        Flowable.fromCallable { torrentSearchRepository.checkAllAsViewed(searchQuery) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun delete(query: String) {
        Flowable.fromCallable { torrentSearchRepository.delete(query) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.subscribeAllSearches()
    }

    fun subscribeSearch(query: String): Flowable<TorrentSearch> {
        return torrentSearchRepository.subscribeSearch(query)
    }
}

interface TorrentSearchRepository {
    fun delete(searchQuery: String)
    fun subscribeSearch(searchQuery: String): Flowable<TorrentSearch>
    fun subscribeAllSearches(): Flowable<List<TorrentSearch>>
    fun insertOrUpdate(searchQuery: String, dataList: List<TorrentData>)
    fun checkAllAsViewed(searchQuery: String)
}

data class TorrentItem(private val item: TorrentData, val isViewed: Boolean) : TorrentDataOwner by item {
    constructor(name: String, torrentUrl: String, isViewed: Boolean) : this(TorrentData(name, torrentUrl), isViewed)
}

data class TorrentSearch(val searchQuery: String, val lastSearchedItems: List<TorrentItem>) {
    val hasUpdates: Boolean
        get() = lastSearchedItems.firstOrNull { !it.isViewed } != null
}