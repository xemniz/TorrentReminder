package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

class SearchUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {

    fun addNewItem(searchQuery: String) {
        Single.fromCallable { torrentSearchRepository.insert(searchQuery, emptyList()) }
                .flatMapCompletable { id ->
                    Single.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                            .flatMapCompletable { Completable.fromAction { torrentSearchRepository.update(id, searchQuery, it) } }
                }
                .subscribeOn(Schedulers.io())
                .onErrorComplete()
                .subscribe()
    }

    fun search(searchQuery: String): Flowable<List<TorrentData>> {
        return Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .subscribeOn(Schedulers.io())
    }

    fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.subscribeAllSearches()
    }

}