package ru.xmn.torrentreminder.features.torrent


import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
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

    fun firstSearch(id: String, searchQuery: String): Completable {
        return search(id, searchQuery).doOnError { torrentSearchRepository.update(id, searchQuery, emptyList()) }
    }

    private fun search(id: String, searchQuery: String): Completable {
        return Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .flatMapCompletable { Completable.fromCallable { torrentSearchRepository.update(id, searchQuery, it) } }
    }

    fun checkAllAsViewed(searchQuery: String) {
        Completable.fromCallable { torrentSearchRepository.checkAllItemsInSearchAsViewed(searchQuery) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun updateItems(): Completable {
        return subscribeAllSearches()
                .take(1)
                .flatMap { Flowable.fromIterable(it) }
                .flatMapCompletable { search(it.id, it.searchQuery) }
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

enum class UpdateItemsResult {
    SUCCESS, ERROR
}
