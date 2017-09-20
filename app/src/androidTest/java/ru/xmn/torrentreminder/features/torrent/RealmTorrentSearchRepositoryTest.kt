package ru.xmn.torrentreminder.features.torrent

import android.os.Handler
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.xml.datatype.DatatypeConstants.SECONDS
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class RealmTorrentSearchRepositoryTest {
    val searchRepository = RealmTorrentSearchRepository()
    lateinit var realm: Realm

    @Before
    fun setUp() {
        val testConfig = RealmConfiguration.Builder().name("test-realm").build()
        Realm.init(InstrumentationRegistry.getTargetContext())
        Realm.setDefaultConfiguration(testConfig)
        realm = Realm.getDefaultInstance()

        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun delete() {
        searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        searchRepository.delete("searchQuery")

        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(0, realmList.size)


        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @Test
    fun insertOrUpdate() {
        searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        Thread.sleep(1000)
        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList.size)
        assertEquals(TorrentSearch("searchQuery", listOf(
                TorrentItem(TorrentData("1", "1"), false),
                TorrentItem(TorrentData("2", "2"), false)
        )), realmList[0].fromRealm())


        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @Test
    fun checkAllAsViewed() {
        searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        searchRepository.checkAllAsViewed("searchQuery")

        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList.size)
        assertEquals(
                TorrentSearch("searchQuery", listOf(
                        TorrentItem(TorrentData("1", "1"), true),
                        TorrentItem(TorrentData("2", "2"), true)
                )), realmList[0].fromRealm())


        searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))

        val realmList2 = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList2.size)
        assertEquals(
                TorrentSearch("searchQuery", listOf(
                        TorrentItem(TorrentData("1", "1"), true),
                        TorrentItem(TorrentData("2", "2"), true),
                        TorrentItem(TorrentData("3", "3"), false)
                )), realmList[0].fromRealm())

        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

    }

    @UiThreadTest
    @Test
    fun testSynchronize() {
        val realm = Realm.getDefaultInstance()

        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}

        Handler().postDelayed({
            val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
            assertEquals(1, realmList.size)
            assertEquals(
                    TorrentSearch("searchQuery", listOf(
                            TorrentItem(TorrentData("1", "1"), true),
                            TorrentItem(TorrentData("2", "2"), true),
                            TorrentItem(TorrentData("3", "3"), false)
                    )), realmList[0].fromRealm())

            realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

        }, 1000)
        Thread.sleep(1100)
    }

    @UiThreadTest
    @Test
    fun testSynchronizeStress() {
        val realm = Realm.getDefaultInstance()

        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}

        Handler().postDelayed({
            val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
            assertEquals(1, realmList.size)
            assertEquals(
                    TorrentSearch("searchQuery", listOf(
                            TorrentItem(TorrentData("1", "1"), true),
                            TorrentItem(TorrentData("2", "2"), true),
                            TorrentItem(TorrentData("3", "3"), true)
                    )), realmList[0].fromRealm())

            realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

        }, 1000)
        Thread.sleep(1100)
    }

    @UiThreadTest
    @Test
    fun testSynchronizeDelete() {
        val realm = Realm.getDefaultInstance()

        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllAsViewed("searchQuery")}
        Handler().post{searchRepository.insertOrUpdate("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.delete("searchQuery")}

        Handler().postDelayed({
            val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
            assertEquals(0, realmList.size)

            realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

        }, 1000)
        Thread.sleep(1100)
    }
}