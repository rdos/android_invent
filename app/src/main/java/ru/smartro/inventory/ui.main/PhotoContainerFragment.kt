package ru.smartro.inventory.ui.main

import android.net.Uri
import ru.smartro.inventory.database.ImageRealmEntity
import java.io.File
import java.lang.Exception

class PhotoContainerFragment : AbstractPhotoFraG() {

    companion object {
        fun newInstance(platformUuid: String, containerUuid: String): PhotoContainerFragment {
            val fragment = PhotoContainerFragment()
            fragment.addArgument(platformUuid, containerUuid)
            return fragment
        }
    }

    override fun onNextClick() {
        // TODO: 22.11.2021 copy-past !
        val file = getOutputDirectory(p_platform_uuid, p_container_uuid)
        val files: Array<File> = file.listFiles()

        val existingImageS = db().getImages(p_container_uuid!!)

        for (inFile in files) {
            val name = inFile.nameWithoutExtension
            val isNameContains = existingImageS.any { el -> el.name == name }
            if(isNameContains)
                continue

            val imageRealmEntity = ImageRealmEntity(p_container_uuid!!)
            try {
                val uri = Uri.fromFile(inFile)

                val imageInBase64 = imageToBase64(uri)
                imageRealmEntity.name = name
                imageRealmEntity.imageBase64 = imageInBase64
                log.info("onNextClick ${inFile.name}")
                if (inFile.isDirectory()) {
                   log.info("onNextClick.isDirectory")
                }

            } catch (e: Exception) {
                log.error("onNextClick", e)
            }
            db().saveRealmEntity(imageRealmEntity)
        }
        showNextFragment(PlatformFragmentContainerDlt.newInstance(p_platform_uuid, p_container_uuid!!))
    }

    override fun onBackPressed() {
        try {

        } catch (e: Exception) {

        }
        db().safeDeleteContainerEntity(p_platform_uuid, p_container_uuid!!)
        callOnBackPressed(false)
    }
}