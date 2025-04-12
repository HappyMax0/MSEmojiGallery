package com.happymax.msemojigallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiListAdapter(val list: List<Emoji>) : RecyclerView.Adapter<EmojiListAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    private var onClickListener: OnClickListener? = null

    // Set the click listener for the adapter
    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    // Interface for the click listener
    interface OnClickListener {
        fun onClick(position: Int, model: Emoji)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val emojiImageImageView: ImageView = view.findViewById(R.id.emojiImageImageView)
        val emojiNameTextView: TextView

        init {
            emojiNameTextView = view.findViewById(R.id.emojiNameTextView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.emoji_item, viewGroup, false)
        val viewHolder = ViewHolder(view)

        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.emojiImageImageView.setImageBitmap(list[position].image3D)
        viewHolder.emojiImageImageView.contentDescription = list[position].name
        viewHolder.emojiNameTextView.text = list[position].name
        viewHolder.emojiNameTextView.contentDescription = list[position].name

        // Set click listener for the item view
        viewHolder.itemView.setOnClickListener {
            onClickListener?.onClick(position, list[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = list.size

}