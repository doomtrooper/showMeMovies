package com.example.showmemovies.utils

import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.TVMEDIACATEGORY
import com.example.showmemovies.models.TvMediaResponseContainer
import com.example.showmemovies.utils.NetworkResponseWrapper

data class FeedApiMapper(
    val feedMovieMediaApiMapper: Map<MEDIACATEGORY, suspend () -> NetworkResponseWrapper<MediaResponseContainer>>,
    val feedTvMediaApiMapper: Map<TVMEDIACATEGORY, suspend () -> NetworkResponseWrapper<TvMediaResponseContainer>>
)