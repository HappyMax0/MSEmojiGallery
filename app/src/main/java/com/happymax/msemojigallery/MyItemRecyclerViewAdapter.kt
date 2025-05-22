package com.happymax.msemojigallery

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.happymax.msemojigallery.EmojiListAdapter.OnClickListener
import com.happymax.msemojigallery.databinding.FragmentSearchResultBinding

class MyItemRecyclerViewAdapter(
    private var items: List<Emoji>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    // Set the click listener for the adapter
    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageBitmap(item.image3D)
        holder.nameView.text = item.name

        // Set click listener for the item view
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(binding: FragmentSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageView
        val nameView: TextView = binding.nameTextView

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

    fun updateData(emojis: List<Emoji>) {
        items = emojis
        notifyDataSetChanged()
    }


}