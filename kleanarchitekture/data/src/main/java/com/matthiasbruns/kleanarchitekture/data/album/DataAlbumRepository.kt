package com.matthiasbruns.kleanarchitekture.data.album

import com.matthiasbruns.kleanarchitekture.data.album.mapper.AlbumEntryMapper
import com.matthiasbruns.kleanarchitekture.data.album.model.AlbumEntry
import com.matthiasbruns.kleanarchitekture.data.photo.PhotoRemote
import com.matthiasbruns.kleanarchitekture.data.photo.mapper.PhotoEntryMapper
import com.matthiasbruns.kleanarchitekture.domain.album.AlbumRepository
import com.matthiasbruns.kleanarchitekture.domain.album.model.Album
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class DataAlbumRepository @Inject constructor(private val albumRemote: AlbumRemote,
                                              private val albumMapper: AlbumEntryMapper,
                                              private val photoRemote: PhotoRemote,
                                              private val photoMapper: PhotoEntryMapper) : AlbumRepository {

    override fun fetch(id: Int): Maybe<Album> =
            albumRemote.fetch(id)
                    .flatMapSingle(this::fetchPhotos)
                    .toMaybe()

    override fun fetchByUser(userId: Int, desc: Boolean): Single<List<Album>> = albumRemote.fetchByUser(userId)
            .map {
                when (desc) {
                    true -> it.reversed()
                    false -> it
                }
            }
            .toObservable()
            .flatMapIterable { it -> it }
            .flatMapSingle(this::fetchPhotos)
            .toList()

    override fun fetchByUser(userId: Int, max: Int, desc: Boolean): Single<List<Album>> = albumRemote.fetchByUser(userId)
            .map {
                when (desc) {
                    true -> it.reversed()
                    false -> it
                }
            }
            .map { it.take(max) }
            .toObservable()
            .flatMapIterable { it -> it }
            .flatMapSingle(this::fetchPhotos)
            .toList()

    private fun fetchPhotos(album: AlbumEntry): Single<Album> =
            photoRemote.fetchByAlbum(album.id)
                    .map { it.map(photoMapper::map) }
                    .map { photos ->
                        albumMapper.map(album, photos)
                    }
}