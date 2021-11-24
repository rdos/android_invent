package ru.smartro.inventory.ui.main

import android.R.attr
import android.R.attr.path
import android.net.Uri
import ru.smartro.inventory.database.ImageRealmEntity
import java.io.File


class PhotoPlatformFragment(val p_platform_id: Int) : AbstractPhotoFragment(p_platform_id, null) {

    companion object {
        fun newInstance(platform_id: Int) = PhotoPlatformFragment(platform_id)
    }

    override fun onNextClick() {
        // TODO: 22.11.2021 copy-past !
        val file = getOutputDirectory(p_platform_id, null)
        val files: Array<File> = file.listFiles()
        val platformEntity = db().loadPlatformEntity(p_platform_id)
        platformEntity.imageList.clear()
        for (inFile in files) {
            log.debug("onNextClick ${inFile.name}")
            if (inFile.isDirectory()) {
                log.debug("onNextClick.isDirectory")
                continue
            }
            val uri = Uri.fromFile(inFile)
            val imageInBase64 = imageToBase64(uri, 0f)
            val imageRealmEntity = ImageRealmEntity()
            imageRealmEntity.imageBase64 = imageInBase64
            platformEntity.imageList.add(imageRealmEntity)
        }
        db().saveRealmEntity(platformEntity)
        showNextFragment(PlatformFragment.newInstance(p_platform_id))
    }
}