package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subscribers.TestSubscriber
import org.junit.Test

import org.junit.Assert.*
import ru.xmn.torrentreminder.features.torrent.domain.*

class SavedSearchServiceUseCaseTest {
    @Test
    fun updateAllItems() {
        val testSubscriber = TestSubscriber.create<ArrayList<TorrentSearch>>()

        SavedSearchServiceUseCase(getSearcher(), getRepository())
                .updateAllItems()
                .toFlowable()
                .subscribe(testSubscriber)

        testSubscriber.awaitCount(1)
        testSubscriber.assertValue(newItems.filter { it.id == "3" } as ArrayList<TorrentSearch>)
    }

    private fun getRepository(): TorrentSearchRepository {
        return object : TorrentSearchRepository {
            val searches: BehaviorProcessor<List<TorrentSearch>> = BehaviorProcessor.create()

            private val oldItems = listOf(
                    TorrentSearch(
                            1, "1", "", listOf(
                            TorrentItem("1", "", true),
                            TorrentItem("2", "", true),
                            TorrentItem("3", "", true))
                    ),
                    TorrentSearch(
                            1, "2", "", listOf(
                            TorrentItem("1", "", true),
                            TorrentItem("2", "", true),
                            TorrentItem("3", "", true))
                    ),
                    TorrentSearch(
                            1, "3", "", listOf(
                            TorrentItem("1", "", true),
                            TorrentItem("2", "", true),
                            TorrentItem("3", "", true))
                    )
            )

            init {
                searches.onNext(oldItems)
            }

            override fun delete(id: String) {
            }

            override fun subscribeSearch(id: String): Flowable<TorrentSearch> {
                return Flowable.empty()
            }

            override fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
                return searches
            }

            override fun insert(searchQuery: String, dataList: List<TorrentData>): String {
                return ""
            }

            override fun update(id: String, searchQuery: String, dataList: List<TorrentData>) {
                if (id == "3") {
                    val newItems = oldItems.map {
                        if (it.id == "3")
                            it.copy(lastSearchedItems = it.lastSearchedItems + TorrentItem("4", "", true))
                        else
                            it
                    }
                    searches.onNext(newItems)
                }
            }

            override fun checkAllItemsInSearchAsViewed(id: String) {
            }
        }
    }

    private fun getSearcher(): TorrentSearcher {
        return object : TorrentSearcher {
            override fun searchTorrents(query: String): List<TorrentData> {
                return emptyList()
            }

        }
    }

    private val newItems = arrayListOf(
            TorrentSearch(
                    1, "1", "", listOf(
                    TorrentItem("1", "", true),
                    TorrentItem("2", "", true),
                    TorrentItem("3", "", true))
            ),
            TorrentSearch(
                    1, "2", "", listOf(
                    TorrentItem("1", "", true),
                    TorrentItem("2", "", true),
                    TorrentItem("3", "", true))
            ),
            TorrentSearch(
                    1, "3", "", listOf(
                    TorrentItem("1", "", true),
                    TorrentItem("2", "", true),
                    TorrentItem("3", "", true),
                    TorrentItem("4", "", true))
            ))

}