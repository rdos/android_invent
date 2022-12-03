package ru.smartro.inventory.ui.main

import android.net.Uri
import ru.smartro.inventory.database.ImageRealmEntity
import java.io.File


class PhotoPlatformFragment : AbstractPhotoFraG() {

    companion object {
        fun newInstance(platformUuid: String): PhotoPlatformFragment {
            val fragment = PhotoPlatformFragment()
            fragment.addArgument(platformUuid, null)
            return fragment
        }
    }

    override fun onNextClick() {
        // TODO: 22.11.2021 copy-past !
        val file = getOutputDirectory(p_platform_uuid, null)
        val files: Array<File> = file.listFiles()
        for (inFile in files) {
            log.debug("onNextClick ${inFile.name}")
            if (inFile.isDirectory()) {
                log.debug("onNextClick.isDirectory")
                continue
            }

            val imageRealmEntity = ImageRealmEntity(p_platform_uuid)
            try {
                val uri = Uri.fromFile(inFile)
                val imageInBase64 = imageToBase64(uri)
                imageRealmEntity.imageBase64 = imageInBase64
            } catch (e: Exception) {
                log.error("onNextClick", e)
            }
            db().saveRealmEntity(imageRealmEntity)
        }
        showNextFragment(PlatformFragment.newInstance(p_platform_uuid))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {

        } catch (e: java.lang.Exception) {

        }
        deleteOutputDirectory(p_platform_uuid, null)
    }
}