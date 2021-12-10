package ru.smartro.inventory.ui.main

import android.net.Uri
import ru.smartro.inventory.database.ImageRealmEntity
import java.io.File
import java.lang.Exception

class PhotoContainerFragment : AbstractPhotoFragment() {

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
        val containerEntity = db().loadContainerEntity(p_container_uuid!!)
        containerEntity.imageList.clear()
        for (inFile in files) {
            try {
                val uri = Uri.fromFile(inFile)

                val imageInBase64 = imageToBase64(uri, 0f)
                val imageRealmEntity = ImageRealmEntity()
                imageRealmEntity.imageBase64 = imageInBase64
                containerEntity.imageList.add(imageRealmEntity)
                log.info("onNextClick ${inFile.name}")
                if (inFile.isDirectory()) {
                   log.info("onNextClick.isDirectory")
                }
            } catch (e: Exception) {
                log.error("onNextClick", e)
            }
        }
        db().saveRealmEntity(containerEntity)
        showNextFragment(PlatformFragmentContainerDlt.newInstance(p_platform_uuid, p_container_uuid!!))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {

        } catch (e: Exception) {

        }
        db().deleteContainerEntity(p_container_uuid!!)
    }
}