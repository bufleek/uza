package com.uza.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.uza.R
import com.uza.adapters.ShopListAdapter
import com.uza.viewmodels.ShopsViewModel
import kotlinx.android.synthetic.main.fragment_shops.*
import kotlinx.android.synthetic.main.shop_dialog.view.*
import kotlinx.android.synthetic.main.shop_dialog.view.name
import kotlinx.android.synthetic.main.shop_dialog.view.price
import kotlinx.android.synthetic.main.shop_list_item.view.*

class ShopsFragment : Fragment() {
    private val viewModel: ShopsViewModel by viewModels()
    private val ref = FirebaseStorage.getInstance().reference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.getPosts()
        return inflater.inflate(R.layout.fragment_shops, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val options = RequestOptions().placeholder(getProgressDrawable()).error(R.drawable.ic_error)
        viewModel.posts.observe(viewLifecycleOwner, {
            shops_recycler.apply {
                setHasFixedSize(true)
                adapter = ShopListAdapter(it) { it ->
                    val view =
                        LayoutInflater.from(requireContext()).inflate(R.layout.shop_dialog, null)
                    view.name.text = it.title
                    view.price.text = "Ksh. ${it.price}"
                    view.description.text = it.description
                    when(it.available){
                        true -> {
                            view.availability.setTextColor(resources.getColor(R.color.success))
                            view.availability.text = "Available"
                        }
                        else -> view.availability.text = "Not Available"
                    }
                    ref.child(it.images[0]).downloadUrl.addOnSuccessListener {
                        Log.d("Uri", "onViewCreated: $it")
                        Glide.with(context).setDefaultRequestOptions(options).load(it).into(view.shop_image)
                    }
                    AlertDialog.Builder(requireContext()).setView(view).show()
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