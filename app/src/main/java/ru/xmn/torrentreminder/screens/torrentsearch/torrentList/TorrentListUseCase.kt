package ru.xmn.torrentreminder.screens.torrentsearch.torrentList

import io.reactivex.Completable
import io.reactivex.Flowable
import ru.xmn.torrentreminder.features.torrent.*
import javax.inject.Inject

/**
 * Created by Michael on 29.09.2017.
 *
 */
class TorrentListUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentListRepository: TorrentSearchRepository){


    fun getTorrentList(query: String): Flowable<TorrentSearch>? {
        return subscribeAllSearches()
                .take(1)
                .flatMap { Flowable.fromIterable(it) }
                .filter { it.searchQuery == query }
    }

    fun firstSearchOnItem(id: String, searchQuery: String): Completable {
        return Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .flatMapCompletable { Completable.fromCallable { torrentListRepository.update(id, searchQuery, it) } }
                .doOnError { torrentListRepository.update(id, searchQuery, emptyList()) }
    }

    private fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentListRepository.subscribeAllSearches()
    }


}