package com.hoony.musicplayerexample.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.data.Music

class MusicListAdapter(private val musicList: List<Music>) :
    RecyclerView.Adapter<MusicItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicItemViewHolder {
        return MusicItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_music,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: MusicItemViewHolder, position: Int) {
        val music = musicList[position]
        holder.bind(music)
    }

}