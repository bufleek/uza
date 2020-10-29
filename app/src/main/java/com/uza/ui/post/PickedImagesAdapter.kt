package com.uza.ui.post

import android.app.AlertDialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uza.R
import kotlinx.android.synthetic.main.picked_post_image.view.*

class PickedImagesAdapter: RecyclerView.Adapter<PickedImagesAdapter.ViewHolder>() {
    private var pickedImages: ArrayList<Bitmap> = ArrayList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val builder = AlertDialog.Builder(it.context)
                val view = LayoutInflater.from(it.context).inflate(R.layout.picked_post_image, it.findViewById(android.R.id.content))
                view.postImage.setImageBitmap(pickedImages[adapterPosition])

                builder.setView(view)
                val dialog = builder.create()
                view.close.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        dialog.dismiss()
                    }
                }
                view.remove.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        pickedImages.removeAt(adapterPosition)
                        dialog.dismiss()
                        notifyDataSetChanged()
                    }
                }
                dialog.show()
            }
        }
        fun bind(position: Int){
            itemView.postImage.setImageBitmap(pickedImages[position])
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.picked_post_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return pickedImages.size
    }

    fun changeData(pickedImages: ArrayList<Bitmap>) {
        this.pickedImages = pickedImages
        notifyDataSetChanged()
    }
}