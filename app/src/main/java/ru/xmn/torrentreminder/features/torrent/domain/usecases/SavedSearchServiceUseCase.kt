package ru.xmn.torrentreminder.features.torrent.domain.usecases

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.xmn.common.extensions.log
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearcher
import javax.inject.Inject

class SavedSearchServiceUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    fun updateAllItems(): Single<ArrayList<TorrentSearch>> {
        val oldItems = getCurrentSavedSearches()
        log("updateAllItems() called")
        return oldItems.flatMap { search ->
            Flowable.fromCallable { torrentSearcher.searchTorrents(search.searchQuery) }
                    .flatMap { resultList ->
                        Log.d(TAG, "$search update success")
                        torrentSearchRepository.update(search.id, search.searchQuery, resultList)
                        Flowable.just(true)
                    }
                    .onErrorReturn {
                        Log.d(TAG, "$search update error")
                        false
                    }
                    .subscribeOn(Schedulers.io())
        }
                //сначала обновляем всё, потом сравниваем с обновленным результатом
                .toList()
                .toFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Flowable.zip<TorrentSearch, TorrentSearch, Result>(
                            oldItems.sorted { s1, s2 -> searchesComparator(s1, s2) },
                            getCurrentSavedSearches().sorted { s1, s2 -> searchesComparator(s1, s2) },
                            BiFunction { t1, t2 ->
                        return@BiFunction convertToResult(t1, t2)
                    })
                }
                .doOnNext {
                    Log.d(TAG, "finish update $it")
                }
                .reduce(ArrayList<TorrentSearch>(), { list, result -> if (result is Result.hasUpdates) list.add(result.search); list })
    }

    private fun convertToResult(t1: TorrentSearch, t2: TorrentSearch): Result {
        return if (t1.id == t2.id && t1 != t2) Result.hasUpdates(t2) else Result.hasNotUpdates
    }

    private fun searchesComparator(s1: TorrentSearch, s2: TorrentSearch) =
            (s1.createdAt - s2.createdAt).toInt()

    private fun getCurrentSavedSearches(): Flowable<TorrentSearch> {
        return torrentSearchRepository.subscribeAllSearches()
                .take(1)
                .flatMap { Flowable.fromIterable(it) }
                .cache()
    }

    sealed class Result {
        class hasUpdates(val search: TorrentSearch) : Result()
        object hasNotUpdates : Result()
    }

    companion object {
        val TAG = "SavedSearchService"
    }
}