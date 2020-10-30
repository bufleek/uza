package com.uza.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.uza.R
import com.uza.adapters.ProductListAdapter
import com.uza.ui.chat.ChatMainActivity
import com.uza.viewmodels.ShopsViewModel
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.product_dialog.view.*

class ProductsFragment : Fragment() {
    private val viewModel: ShopsViewModel by viewModels()
    private val ref = FirebaseStorage.getInstance().reference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.getPosts()
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val options = RequestOptions().placeholder(getProgressDrawable()).error(R.drawable.ic_error)
        viewModel.posts.observe(viewLifecycleOwner, {
            shops_recycler.apply {
                setHasFixedSize(true)
                adapter = ProductListAdapter(it) { post ->
                    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.product_dialog, null)
                    val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).show()
                    dialogView.name.text = post.title
                    dialogView.price.text = "Ksh. ${post.price}"
                    dialogView.description.text = post.description
                    when(post.available){
                        true -> {
                            dialogView.availability.setTextColor(resources.getColor(R.color.success))
                            dialogView.availability.text = "Available"
                        }
                        else -> {
                            dialogView.availability.text = "Not Available"
                            dialogView.btn_buy.isEnabled = false
                        }
                    }
                    ref.child(post.images[0]).downloadUrl.addOnSuccessListener {
                        Glide.with(context).setDefaultRequestOptions(options).load(it).into(dialogView.shop_image)
                    }
                    dialogView.btn_buy.setOnClickListener {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Starting Chat With Seller", Toast.LENGTH_LONG).show()
                        val intent = Intent(context, ChatMainActivity::class.java)
                        intent.putExtra("chatWith", post.sellerId)
                        startActivity(intent)
                    }
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun getProgressDrawable(): CircularProgressDrawable {
        return CircularProgressDrawable(requireContext()).apply {
            strokeWidth = 5f
            centerRadius = 25f
            start()
        }
    }
}