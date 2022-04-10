package com.example.pictures

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var editText: EditText
    private lateinit var pasteAddressButton: Button
    private lateinit var androidLoadButton: Button
    private lateinit var picassoLoadButton: Button
    private lateinit var glideLoadButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setViewClickListeners()
    }

    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        pasteAddressButton = findViewById(R.id.paste_test_http_button)
        androidLoadButton = findViewById(R.id.android_load_button)
        glideLoadButton = findViewById(R.id.glide_load_button)
        picassoLoadButton = findViewById(R.id.picasso_load_button)
    }

    private fun setViewClickListeners() {
        pasteAddressButton.setOnClickListener {
            editText.setText(R.string.web_address)
        }

        androidLoadButton.setOnClickListener {
            imageView.setImageDrawable(null)
            thread(start = true) {
                try {
                    val newUrl = URL(editText.text.toString())
                    val image = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream())
                    runOnUiThread {
                        imageView.setImageBitmap(image)
                    }
                } catch (e: IOException) {
                    runOnUiThread {
                        showToastWithError()
                    }
                }
            }
        }

        glideLoadButton.setOnClickListener {
            Glide.with(this)
                .load(editText.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        showToastWithError()
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(imageView)
        }

        picassoLoadButton.setOnClickListener {
            if (editText.text.trim().isEmpty()) {
                showToastWithError()
            } else {
                Picasso.get()
                    .load(editText.text.toString())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(imageView, object : Callback {
                        override fun onError(e: Exception?) {
                            showToastWithError()
                        }

                        override fun onSuccess() {
                            return
                        }
                    })
            }
        }
    }

    private fun showToastWithError() {
        Toast.makeText(this, R.string.error_picture_not_loaded, Toast.LENGTH_SHORT)
            .show()
    }

}