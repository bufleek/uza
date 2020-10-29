package com.uza.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.uza.R
import com.uza.models.Post
import kotlinx.android.synthetic.main.product_list_item.view.*

class ProductListAdapter(private val data: List<Post>, val viewItem: (post: Post) -> Unit): RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ProductListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val context = itemView.context
        private val ref = FirebaseStorage.getInstance().reference
        val options = RequestOptions().placeholder(getProgressDrawable()).error(R.drawable.ic_error)

        fun bind(post: Post) {
            ref.child(post.images[0]).downloadUrl.addOnSuccessListener {
                Glide.with(context).setDefaultRequestOptions(options).load(it).into(itemView.image)
            }
            itemView.name.text = post.title
            itemView.price.text = "Ksh. ${post.price}"
            itemView.setOnClickListener { viewItem(post) }
        }

       private fun getProgressDrawable(): CircularProgressDrawable {
            return CircularProgressDrawable(context).apply {
                strokeWidth = 5f
                centerRadius = 25f
                start()
            }
        }

    }
}