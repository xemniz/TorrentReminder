package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

class SearchUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    val searchIsSaved: BehaviorProcessor<Boolean> = BehaviorProcessor.createDefault(false)

    fun addNewItem(searchQuery: String) {
        Single.fromCallable { torrentSearchRepository.insert(searchQuery, emptyList()) }
                .flatMapCompletable { id ->
                    Single.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                            .flatMapCompletable {
                                Completable.fromAction {
                                    torrentSearchRepository.update(id, searchQuery, it)
                                    torrentSearchRepository.checkAllItemsInSearchAsViewed(id)
                                }
                            }
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

    fun bindQueryFlowable(searchQueryFlowable: Flowable<String>) {
        Flowable.combineLatest<String, List<TorrentSearch>, Pair<String, List<TorrentSearch>>>(
                searchQueryFlowable,
                subscribeAllSearches(),
                BiFunction { query: String, searches: List<TorrentSearch> ->
                    Pair(query, searches)
                })
                .map {
                    val (query, searches) = it
                    searches.any { it.searchQuery == query }
                }
                .subscribe(searchIsSaved)
    }

}