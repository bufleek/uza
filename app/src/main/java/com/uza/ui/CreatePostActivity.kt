package com.uza.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uza.R
import kotlinx.android.synthetic.main.activity_create_post.*


class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        btn_pick_image.setOnClickListener {
        }
    }
}