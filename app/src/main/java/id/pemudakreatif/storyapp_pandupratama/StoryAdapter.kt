//package id.pemudakreatif.storyapp_pandupratama
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide


package id.pemudakreatif.storyapp_pandupratama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide

class StoryAdapter(private val onItemClick: (Story) -> Unit) : PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)

        fun bind(story: Story) {
            nameTextView.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(photoImageView)

            // Menangani klik pada item
            itemView.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}


//class StoryAdapter(private val stories: List<Story>, private val onItemClick: (Story) -> Unit) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
//        return StoryViewHolder(itemView)
//    }
//    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
//        val story = stories[position]
//        holder.bind(story)
//    }
//    override fun getItemCount(): Int {
//        return stories.size
//    }
//    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
//        private val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
//        fun bind(story: Story) {
//            nameTextView.text = story.name
//            Glide.with(itemView.context)
//                .load(story.photoUrl)
//                .into(photoImageView)
//            itemView.setOnClickListener {
//                onItemClick(story)
//            }
//        }
//    }
//}
