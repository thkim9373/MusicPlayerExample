package com.hoony.musicplayerexample.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.data.Album

class AlbumPagerAdapter(private val albumList: List<Album>) :
    RecyclerView.Adapter<PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_album,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val album = albumList[position]
        holder.bind(album)
    }

}