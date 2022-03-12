package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ReprsentativeListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative


class RepresentativeListAdapter
    : ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val representativeItem = getItem(position)
        holder.bind(representativeItem)
    }
}


class RepresentativeViewHolder(val binding: ReprsentativeListItemBinding): RecyclerView.ViewHolder(binding.root) {


    fun bind(item: Representative) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)
        item.official.channels?.let { showSocialLinks(it) }
        item.official.urls?.let { showWWWLinks(it) }
        binding.executePendingBindings()
    }


    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ReprsentativeListItemBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }

    private fun showSocialLinks(channels: List<Channel>) {

        if (!channels.isNullOrEmpty()) {

            // Determine whether we show a Facebook icon
            val facebookUrl = getFacebookUrl(channels)
            if (!facebookUrl.isNullOrBlank()) {
                enableLink(binding.facebookIcon, facebookUrl)
            } else {
                binding.facebookIcon.visibility = View.INVISIBLE
            }

            // Determine whether we show a Twitter icon
            val twitterUrl = getTwitterUrl(channels)
            if (!twitterUrl.isNullOrBlank()) {
                enableLink(binding.twitterIcon, twitterUrl)
            } else {
                binding.twitterIcon.visibility = View.INVISIBLE
            }
        } else {
            binding.twitterIcon.visibility = View.INVISIBLE
            binding.facebookIcon.visibility = View.INVISIBLE
        }
    }


    private fun showWWWLinks(urls: List<String>) {

        if (!urls.isNullOrEmpty()) {
            enableLink(binding.wwwIcon, urls.first())
        } else {
            binding.wwwIcon.visibility = View.INVISIBLE
        }
    }


    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }


    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }


    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }


    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}


class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {


    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return ((oldItem.office == newItem.office) && (oldItem.official == newItem.official))
    }


    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }
}
