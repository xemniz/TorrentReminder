package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TorrentSearchUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {

    fun addEmptyItem() {
        Completable.fromCallable { torrentSearchRepository.insert("", emptyList()) }
                .subscribeOn(Schedulers.io())
                .subscribe { }
    }

    fun search(id: String, searchQuery: String) {
        Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .subscribeOn(Schedulers.io())
                .subscribe { torrentSearchRepository.update(id, searchQuery, it) }
    }

    fun checkAllAsViewed(searchQuery: String) {
        Completable.fromCallable { torrentSearchRepository.checkAllItemsInSearchAsViewed(searchQuery) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun delete(query: String) {
        Completable.fromCallable { torrentSearchRepository.delete(query) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.subscribeAllSearches()
    }

    fun subscribeSearch(id: String): Flowable<TorrentSearch> {
        return torrentSearchRepository.subscribeSearch(id)
    }

}