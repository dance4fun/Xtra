package com.github.andreyasadchy.xtra.ui.videos.game


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.model.helix.video.BroadcastType
import com.github.andreyasadchy.xtra.model.helix.video.Period
import com.github.andreyasadchy.xtra.model.helix.video.Sort
import com.github.andreyasadchy.xtra.model.helix.video.Video
import com.github.andreyasadchy.xtra.repository.Listing
import com.github.andreyasadchy.xtra.repository.PlayerRepository
import com.github.andreyasadchy.xtra.repository.TwitchService
import com.github.andreyasadchy.xtra.ui.videos.BaseVideosViewModel
import javax.inject.Inject

class GameVideosViewModel @Inject constructor(
        context: Application,
        private val repository: TwitchService,
        playerRepository: PlayerRepository) : BaseVideosViewModel(playerRepository) {

    private val _sortText = MutableLiveData<CharSequence>()
    val sortText: LiveData<CharSequence>
        get() = _sortText
    private val filter = MutableLiveData<Filter>()
    override val result: LiveData<Listing<Video>> = Transformations.map(filter) {
        repository.loadVideos(it.clientId, it.token, it.game, it.period, it.broadcastType, it.language, it.sort, viewModelScope)
    }
    val sort: Sort
        get() = filter.value!!.sort
    val period: Period
        get() = filter.value!!.period

    init {
        _sortText.value = context.getString(R.string.sort_and_period, context.getString(R.string.view_count), context.getString(R.string.this_week))
    }

    fun setGame(clientId: String?, token: String?, game: String) {
        if (filter.value?.game != game) {
            filter.value = Filter(clientId, token, game)
        }
    }

    fun filter(clientId: String?, token: String?, sort: Sort, period: Period, text: CharSequence) {
        filter.value = filter.value?.copy(clientId = clientId, token = token, sort = sort, period = period)
        _sortText.value = text
    }

    private data class Filter(
            val clientId: String?,
            val token: String?,
            val game: String,
            val sort: Sort = Sort.VIEWS,
            val period: Period = Period.WEEK,
            val broadcastType: BroadcastType = BroadcastType.ALL,
            val language: String? = null)
}
