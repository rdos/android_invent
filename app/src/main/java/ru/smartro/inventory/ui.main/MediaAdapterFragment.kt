/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.smartro.inventory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.smartro.inventory.R
import java.io.File


/** Fragment used for each individual page showing a photo inside of [PhotoShowFragment] */
class MediaAdapterFragment internal constructor() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.media_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments ?: return
        val aptvNumOfCount = view.findViewById<AppCompatTextView>(R.id.aptv_photo_show_fragment__num_of_count)
        aptvNumOfCount.text = args.getString(NUM_OF_COUNT)
        val imageView = view.findViewById<AppCompatImageView>(R.id.apiv_photo_show_fragment)
        val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_photo
        Glide.with(view).load(resource).into(imageView)
    }

    companion object {
        private const val FILE_NAME_KEY = "file_name"
        private const val NUM_OF_COUNT = "num_of_count"

        fun create(image: File, numOfCount: String) = MediaAdapterFragment().apply {
            arguments = Bundle().apply {
                putString(FILE_NAME_KEY, image.absolutePath)
                putString(NUM_OF_COUNT, numOfCount)
            }
        }
    }
}