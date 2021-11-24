package ru.smartro.inventory.ui.main

import android.net.Uri
import ru.smartro.inventory.database.ImageRealmEntity
import java.io.File

class PhotoContainerFragment(val p_platform_id: Int, val p_container_id: Int) : AbstractPhotoFragment(p_platform_id, p_container_id) {

    companion object {
        fun newInstance(platform_id: Int, container_id: Int) = PhotoContainerFragment(platform_id, container_id)
    }

    override fun onNextClick() {
        // TODO: 22.11.2021 copy-past !
        val file = getOutputDirectory(p_platform_id, p_container_id)
        val files: Array<File> = file.listFiles()
        val containerEntity = db().loadContainerEntity(p_container_id)
        containerEntity.imageList.clear()
        for (inFile in files) {
            val uri = Uri.fromFile(inFile)
            val imageInBase64 = imageToBase64(uri, 0f)
            val imageRealmEntity = ImageRealmEntity()
            imageRealmEntity.imageBase64 = imageInBase64
            containerEntity.imageList.add(imageRealmEntity)
            log.info("onNextClick ${inFile.name}")
            if (inFile.isDirectory()) {
               log.info("onNextClick.isDirectory")
            }
        }
        db().saveRealmEntity(containerEntity)
        showNextFragment(PlatformFragmentContainerDlt.newInstance(p_container_id))
    }
}