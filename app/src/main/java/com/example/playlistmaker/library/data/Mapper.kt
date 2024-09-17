package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.search.domain.models.Track

fun TrackEntity.toDomainModel(): Track {
    return Track(
        trackID = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        artworkUrl = this.artworkUrl,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.trackUrl
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        trackId = this.trackID,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        artworkUrl = this.artworkUrl,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        trackUrl = this.previewUrl
    )
}

fun PlaylistEntity.toDomainModel(): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        image = this.image,
        count = this.count,
        info = this.info,
        content = this.content
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        name = this.name,
        image = this.image,
        count = this.count,
        info = this.info,
        content = this.content
    )
}

fun Track.PlaylistTrackEntity(): PlaylistTrackEntity {
    return PlaylistTrackEntity(
        trackId = this.trackID,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        artworkUrl = this.artworkUrl,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        trackUrl = this.previewUrl
    )
}
