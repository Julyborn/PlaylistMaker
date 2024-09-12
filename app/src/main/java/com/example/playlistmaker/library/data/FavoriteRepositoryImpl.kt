import android.util.Log
import com.example.playlistmaker.library.data.db.TrackDao
import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.library.domain.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val trackDao: TrackDao
) : FavoriteRepository {

    override suspend fun addTrack(track: TrackEntity) {
        Log.d("БД", "Добавляю трек ${track.trackName}")
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(track)
        }
    }

    override suspend fun removeTrack(track: TrackEntity) {
        Log.d("БД", "Удаляю трек ${track.trackName}")
        withContext(Dispatchers.IO) {
            trackDao.deleteTrack(track.trackId)
        }
    }

    override fun getAllTracks(): Flow<List<TrackEntity>> {
        Log.d("БД", "Выдаю Треки")
        return trackDao.getAllTracks()
    }

    override suspend fun getAllTracksIDs(): List<Int> {
        Log.d("БД", "Выдаю id треков")
        return withContext(Dispatchers.IO) {
            trackDao.getAllTracksIDs()
        }
    }
}
