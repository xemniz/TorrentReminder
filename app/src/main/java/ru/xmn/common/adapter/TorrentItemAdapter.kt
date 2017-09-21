package ru.xmn.common.adapter
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.xmn.torrentreminder.R

/**
 * Created by Michael on 20.09.2017.
 */

data class TorrentItem(val name: String, val updateInfo: String)

@Suppress("UNREACHABLE_CODE")
class TorrentItemAdapter(context: Context, val items: List<TorrentItem>) : RecyclerView.Adapter<TorrentItemAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val torrentItems: TorrentItem = items[position]
        holder.nameView.text = torrentItems.name
        holder.companyView.text = torrentItems.updateInfo

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.list_item, parent, false)
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder internal constructor(view:View):RecyclerView.ViewHolder(view) {
        internal val nameView: TextView = view.findViewById<View>(R.id.name) as TextView
        internal val companyView:TextView = view.findViewById<View>(R.id.updateInfo) as TextView
    }
}