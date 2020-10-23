package com.uza.ui.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.uza.data.models.PostItem
import com.uza.databinding.ActivityCreatePostBinding
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    private lateinit var viewModel: PostViewModel
    private lateinit var pickedImagesAdapter: PickedImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCreatePostBinding = ActivityCreatePostBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        binding.viewModel = viewModel
        setContentView(binding.root)

        pickedImagesAdapter = PickedImagesAdapter()
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        postImagesRecycler.apply {
            adapter = pickedImagesAdapter
            layoutManager = linearLayoutManager
        }
        pickedImagesAdapter.changeData(viewModel.pickedImages)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(postImagesRecycler)

        btn_pick_image.setOnClickListener {
            pickImages()
        }

        postItemButton.setOnClickListener {
            onSubmitPost()
        }
    }

    private fun onSubmitPost() {
        val postItem = viewModel.postItem
        if (postItem.title.trim().isEmpty()){
            postTitle.error = "Title can't be blank"
            return
        }
        if (postItem.description.trim().isEmpty()){
            postDescription.error = "Description can't be blank"
            return
        }
        if (postItem.price.trim().isEmpty()){
            postPrice.error = "Price can't be blank"
            return
        }

        viewModel.uploadPost()
        Toast.makeText(this, "Uploading", Toast.LENGTH_SHORT).show()
        //reset
        viewModel.pickedImages.clear()
        viewModel.postItem = PostItem()
        pickedImagesAdapter.changeData(ArrayList())
        postTitle.setText("")
        postDescription.setText("")
        postPrice.setText("")
        availability.isChecked = false

    }

    private fun pickImages() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            PICK_IMAGES_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data.clipData != null) {
                val mClipData = data.clipData
                val imagesCount = data.clipData?.itemCount
                imagesCount?.let { it ->
                    for (i in 0 until it) {
                        val clipItem = mClipData!!.getItemAt(i)
                        getImageFromUri(clipItem.uri)
                    }
                }
            }else{
                val uri = data.data
                uri?.let {
                    getImageFromUri(uri)
                }
            }
            pickedImagesAdapter.changeData(viewModel.pickedImages)
        }
    }

    private fun getImageFromUri(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(baseContext.contentResolver, imageUri)
            viewModel.pickedImages.add(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PICK_IMAGES_REQUEST_CODE = 100
    }
}