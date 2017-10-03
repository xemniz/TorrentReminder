package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

class SearchFragmentUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {

    fun addNewItem(searchQuery: String) {
        Completable.fromCallable { torrentSearchRepository.insert(searchQuery, emptyList()) }
                .subscribeOn(Schedulers.io())
                .subscribe { }
    }

    fun search(searchQuery: String): Flowable<List<TorrentData>> {
        return Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .subscribeOn(Schedulers.io())
    }

    fun isSearchInRepository(query: String): Flowable<Boolean> {
        return torrentSearchRepository.subscribeAllSearches().take(1).map { it.any { it.searchQuery == query } }
    }



    fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.subscribeAllSearches()
    }

}