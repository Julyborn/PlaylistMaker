import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MiniPlaylistItemBinding
import com.example.playlistmaker.library.domain.playlist.Playlist

class BottomSheetAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    inner class BottomSheetViewHolder(private val binding: MiniPlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val radius: Float = 2 * itemView.resources.displayMetrics.density

        fun bind(model: Playlist) {
            binding.playlistName.text = model.name
            binding.playlistCount.text = bindTracks(model.count)

            Glide.with(itemView.context)
                .load(model.image)
                .transform(CenterCrop(), RoundedCorners(radius.toInt()))
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.playlistImg)

            itemView.setOnClickListener {
                onPlaylistClicked(model)
            }
        }

        private fun bindTracks(count: Int): String {
            return when {
                count in 11..14 -> "$count треков"
                count % 10 == 1 -> "$count трек"
                count % 10 in 2..4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MiniPlaylistItemBinding.inflate(layoutInflater, parent, false)
        return BottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }
}
