package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class TorrentSearchUseCase(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    fun search(searchString: String) {
        Flowable.fromCallable { torrentSearcher.searchTorrents(searchString) }
                .subscribeOn(Schedulers.io())
                .subscribe { torrentSearchRepository.insertOrUpdate(searchString, it) }
    }

    fun checkAllAsViewed() {
        Flowable.fromCallable { torrentSearchRepository.checkAllAsViewed() }
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
    fun delete(result: String)
    fun subscribeSearch(query: String): Flowable<TorrentSearch>
    fun subscribeAllSearches(): Flowable<List<TorrentSearch>>
    fun insertOrUpdate(searchString: String, dataList: List<TorrentData>)
    fun checkAllAsViewed()
}

data class TorrentItem(val item: TorrentData, val isViwed: Boolean)

data class TorrentSearch(val query: String, val lastSearchedItems: List<TorrentItem>) {
    val hasUpdates: Boolean
        get() = lastSearchedItems.firstOrNull { !it.isViwed } != null
}