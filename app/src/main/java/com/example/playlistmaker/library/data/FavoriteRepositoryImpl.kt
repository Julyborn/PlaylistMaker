
import android.util.Log
import com.example.playlistmaker.library.data.db.dao.TrackDao
import com.example.playlistmaker.library.data.toDomainModel
import com.example.playlistmaker.library.data.toEntity
import com.example.playlistmaker.library.domain.favorite.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val trackDao: TrackDao
) : FavoriteRepository {

    override suspend fun addTrack(track: Track) {
        Log.d("БД", "Добавляю трек ${track.trackName}")
        trackDao.insertTrack(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        Log.d("БД", "Удаляю трек ${track.trackName}")
        trackDao.deleteTrack(track.trackID)
    }

    override fun getAllTracks(): Flow<List<Track>> {
        Log.d("БД", "Выдаю Треки")
        return trackDao.getAllTracks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllTrackIDs(): Flow<List<Int>> {
        Log.d("БД", "Выдаю id треков")
        return trackDao.getAllTracksIDs()
    }
}
