package ru.xmn.kotlinstarter.features.torrent

import junit.framework.Assert
import org.junit.Test

/**
 * Created by USER on 19.09.2017.
 */
class TorrentSearcherWithUpdatesTest {
    @Test
    fun searchTorrents() {
        //первый запрос - два новых итема
        val searcher = TorrentSearcherWithUpdates(getTorrentSearcherWithUpdates(), getTorrentSearchLastResultRepository())
        var actual: SearchWithUpdatesResult = searcher.searchTorrents("no matter")
        var expected: SearchWithUpdatesResult = SearchWithUpdatesResult.HasNewItems(firstQuery.map { TorrentItemWithUpdates(it, true) })
        Assert.assertEquals(expected, actual)

        //второй запрос - новые стали старыми, один новый
        actual = searcher.searchTorrents("no matter")
        expected = SearchWithUpdatesResult.HasNewItems(listOf(
                TorrentItemWithUpdates(TorrentItem("name1", "url"), false),
                TorrentItemWithUpdates(TorrentItem("name2", "url"), false),
                TorrentItemWithUpdates(TorrentItem("name3", "url"), true)
        ))
        Assert.assertEquals(expected, actual)

        //третий запрос - все стали старыми
        actual = searcher.searchTorrents("no matter")
        expected = SearchWithUpdatesResult.HasNotNewItems(updatedQuery)
        Assert.assertEquals(expected, actual)
    }

    private fun getTorrentSearchLastResultRepository(): TorrentSearchLastResultRepository {
        return object : TorrentSearchLastResultRepository {
            var lastResult: List<TorrentItem> = emptyList()
            override fun getLastResult(query: String): List<TorrentItem> {
                return lastResult
            }

            override fun setLastResult(query: String, items: List<TorrentItem>) {
                this.lastResult = items
            }

        }
    }

    private val firstQuery: List<TorrentItem> = listOf(
            TorrentItem("name1", "url"),
            TorrentItem("name2", "url")
    )

    private val updatedQuery = listOf(
            TorrentItem("name1", "url"),
            TorrentItem("name2", "url"),
            TorrentItem("name3", "url")
    )

    private fun getTorrentSearcherWithUpdates(): TorrentSearcher {
        return object : TorrentSearcher {
            var isFirstSearch = true
            override fun searchTorrents(query: String): List<TorrentItem> {
                return when {
                    isFirstSearch -> {
                        isFirstSearch = false
                        firstQuery
                    }
                    else -> updatedQuery
                }
            }
        }
    }
}