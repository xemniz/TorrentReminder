package ru.xmn.torrentreminder.features.torrent



import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.TorrentSearchViewModel


class ScheduledJobService : com.firebase.jobdispatcher.JobService() {

    var torrentSearchViewModel: TorrentSearchViewModel = TorrentSearchViewModel()


    override fun onStartJob(job: com.firebase.jobdispatcher.JobParameters?): Boolean {
        torrentSearchViewModel.updateAllItems()
        return true
    }

    override fun onStopJob(job: com.firebase.jobdispatcher.JobParameters?): Boolean {
        return false
    }

}