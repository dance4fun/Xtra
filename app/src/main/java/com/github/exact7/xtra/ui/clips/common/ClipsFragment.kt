package com.github.exact7.xtra.ui.clips.common

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.exact7.xtra.R
import com.github.exact7.xtra.model.game.Game
import com.github.exact7.xtra.ui.clips.BaseClipsFragment
import com.github.exact7.xtra.ui.clips.Period
import com.github.exact7.xtra.util.FragmentUtils
import kotlinx.android.synthetic.main.fragment_clips.*

class ClipsFragment : BaseClipsFragment() {

    private lateinit var viewModel: ClipsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortBar.setOnClickListener { FragmentUtils.showRadioButtonDialogFragment(requireActivity(), childFragmentManager, viewModel.sortOptions, viewModel.selectedIndex) }
    }

    override fun initialize() {
        if (isFragmentVisible) {
            super.initialize()
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(ClipsViewModel::class.java)
            binding.viewModel = viewModel
            binding.sortText = viewModel.sortText
            viewModel.list.observe(this, Observer {
                adapter.submitList(it)
            })
            viewModel.loadClips(arguments?.getString("channel"), arguments?.getParcelable("game") as Game?)
        }
    }

    override fun onNetworkRestored() {
        viewModel.retry()
    }

    override fun onChange(index: Int, text: CharSequence, tag: Int?) {
        var period: Period? = null
        var trending = false
        when (tag) {
            R.string.trending -> trending = true
            R.string.today -> period = Period.DAY
            R.string.this_week -> period = Period.WEEK
            R.string.this_month -> period = Period.MONTH
            R.string.all_time -> period = Period.ALL
        }
        viewModel.filter(period, trending, index, text)
    }
}