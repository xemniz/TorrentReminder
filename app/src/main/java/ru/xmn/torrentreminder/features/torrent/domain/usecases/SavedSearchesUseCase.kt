package ru.xmn.torrentreminder.features.torrent.domain.usecases


import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

class TorrentSearchUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {

    fun addEmptyItem() {
        Completable.fromCallable { torrentSearchRepository.insert("", emptyList()) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun firstSearchOnItem(id: String, searchQuery: String): Completable {
        return Flowable
                .fromCallable { torrentSearchRepository.update(id, searchQuery, emptyList()) }
                .flatMap { Flowable.fromCallable {  torrentSearcher.searchTorrents(searchQuery)  } }
                .flatMapCompletable { Completable.fromCallable { torrentSearchRepository.update(id, searchQuery, it) } }
                .doOnError { torrentSearchRepository.update(id, searchQuery, emptyList()) }
    }

    fun updateItems(): Single<UpdateItemsResult> {
        return subscribeAllSearches()
                .take(1)
                .flatMap { Flowable.fromIterable(it) }
                .flatMap { search ->
                    Flowable.fromCallable { torrentSearcher.searchTorrents(search.searchQuery) }
                            .flatMap { resultList ->
                                torrentSearchRepository.update(search.id, search.searchQuery, resultList)
                                Flowable.just(true)
                            }
                            .onErrorReturn { false }
                            .subscribeOn(Schedulers.io())

                }
                .toList()
                .map {
                    when {
                        it.contains(false) -> UpdateItemsResult.ERROR
                        else -> UpdateItemsResult.SUCCESS
                    }
                }
    }

    fun delete(id: String) {
        Completable.fromCallable { torrentSearchRepository.delete(id) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.subscribeAllSearches()
    }

    fun deleteNewSearch() {
        torrentSearchRepository
                .subscribeAllSearches()
                .take(1)
                .observeOn(Schedulers.io())
                .flatMap { Flowable.fromIterable(it) }
                .filter { it.searchQuery == "" }
                .flatMapCompletable { Completable.fromCallable { torrentSearchRepository.delete(it.id) } }
                .subscribe()
    }
}

enum class UpdateItemsResult {
    SUCCESS, ERROR
}
