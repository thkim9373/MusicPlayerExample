package com.hoony.musicplayerexample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.activity.AudioListAdapter
import com.hoony.musicplayerexample.view_model.AlbumListViewModel
import kotlinx.android.synthetic.main.fragment_album_list.*

class AlbumListFragment : Fragment() {

    private val viewModel by viewModels<AlbumListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
    }

    private fun setRecyclerView() {
        rvAlbumList.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun setObserver() {
        viewModel.albumListLiveData.observe(
            this,
            Observer {
                rvAlbumList.adapter = AudioListAdapter(it)
            }
        )
    }
}