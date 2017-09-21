package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.application.di.scopes.ActivityScope
import javax.inject.Inject

class TorrentSearchUseCase
@ActivityScope
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {

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