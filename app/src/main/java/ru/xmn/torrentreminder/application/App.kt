package ru.xmn.torrentreminder.application

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.xmn.torrentreminder.BuildConfig
import ru.xmn.torrentreminder.application.di.ApplicationComponent
import ru.xmn.torrentreminder.application.di.ApplicationModule
import ru.xmn.torrentreminder.application.di.DaggerApplicationComponent


class App : Application() {

    companion object {
        lateinit var component: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
        initializeRealm()
    }

    private fun initializeRealm() {
        Realm.init(this)

        var configBuilder: RealmConfiguration.Builder = RealmConfiguration.Builder()
                .schemaVersion(1)

        if (BuildConfig.DEBUG)
            configBuilder = configBuilder.deleteRealmIfMigrationNeeded()

        val config = configBuilder
//                .migration(Migration())
                .build()

        Realm.setDefaultConfiguration(config)
    }

    fun initializeDagger() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}