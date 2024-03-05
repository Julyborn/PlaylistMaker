import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.Track

class TrackAdapter(private val context: Context, private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackNameTextView: TextView = itemView.findViewById<TextView>(R.id.track_name)
        private val artistNameTextView: TextView = itemView.findViewById<TextView>(R.id.artist_name)
        private val trackTimeTextView: TextView = itemView.findViewById<TextView>(R.id.track_time)
        private val artworkImageView: ImageView = itemView.findViewById<ImageView>(R.id.album_cover)

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.trackTime

            Glide.with(itemView.context)
                .load(track.artworkUrl100)

                // стоит убрать после спринта 10, код нужен что бы проверить placeholder'ы при запуске без инета
                .skipMemoryCache(true) // Пропустить кэширование в памяти
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Не кэшировать на диске

                .placeholder(R.drawable.ic_search_placeholder)
                .centerCrop()
                .transform(RoundedCorners(2))
                .into(artworkImageView)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
    }
    override fun getItemCount(): Int {
        return tracks.size
    }
}
