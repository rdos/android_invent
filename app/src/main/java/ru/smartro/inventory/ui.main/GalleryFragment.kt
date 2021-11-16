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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.io.File
import android.media.MediaScannerConnection
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractFragment
import java.util.Locale

val EXTENSION_WHITELIST = arrayOf("JPG")
//class GalleryFragment(p_id: Int) internal constructor()
class GalleryFragment(val p_id: Int) : AbstractFragment() {

    companion object {
        fun newInstance(p_id: Int) = GalleryFragment(p_id)
    }

    private lateinit var mediaList: MutableList<File>

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment = MediaAdapterFragment.create(mediaList[position])
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true

        // Get root directory of media from navigation arguments
        val outputDirectory = getOutputDirectory(p_id)
        val rootDirectory = File(outputDirectory.absolutePath)

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.photo_show_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apibDelete =  view.findViewById<AppCompatImageButton>(R.id.apib_photo_show_fragment__delete)
        val viewPager = view.findViewById<ViewPager>(R.id.vp_photo_show_fragment)

        //Checking media files list
        if (mediaList.isEmpty()) {
            apibDelete.isEnabled = false
//            fragmentGalleryBinding.shareButton.isEnabled = false
        }

        // Populate the ViewPager and implement a cache of two media items
        viewPager.apply {
            offscreenPageLimit = 2
            adapter = MediaAdapter(childFragmentManager)
        }

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
//            fragmentGalleryBinding.cutoutSafeArea.padWithDisplayCutout()
        }

        // Handle back button press
//        fragmentGalleryBinding.backButton.setOnClickListener {
//            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
//        }

        // Handle share button press
//        fragmentGalleryBinding.shareButton.setOnClickListener {
//
//            mediaList.getOrNull(fragmentGalleryBinding.photoViewPager.currentItem)?.let { mediaFile ->
//
//                // Create a sharing intent
//                val intent = Intent().apply {
//                    // Infer media type from file extension
//                    val mediaType = MimeTypeMap.getSingleton()
//                            .getMimeTypeFromExtension(mediaFile.extension)
//                    // Get URI from our FileProvider implementation
//                    val uri = FileProvider.getUriForFile(
//                            view.context, BuildConfig.APPLICATION_ID + ".provider", mediaFile)
//                    // Set the appropriate intent extra, type, action and flags
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    type = mediaType
//                    action = Intent.ACTION_SEND
//                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                }
//
//                // Launch the intent letting the user choose which app to share with
//                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
//            }
//        }

        // Handle delete button press
        apibDelete.setOnClickListener {

            mediaList.getOrNull(viewPager.currentItem)?.let { mediaFile ->

                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_dialog))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes) { _, _ ->

                            // Delete current photo
                            mediaFile.delete()

                            // Send relevant broadcast to notify other apps of deletion
                            MediaScannerConnection.scanFile(
                                    view.context, arrayOf(mediaFile.absolutePath), null, null)

                            // Notify our view pager
                            mediaList.removeAt(viewPager.currentItem)
                            viewPager.adapter?.notifyDataSetChanged()

                            // If all photos have been deleted, return to camera
                            if (mediaList.isEmpty()) {
                                this.exitFragment()
                            }
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .create().showImmersive()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
