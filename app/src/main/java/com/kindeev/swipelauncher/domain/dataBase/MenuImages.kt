package com.kindeev.swipelauncher.domain.dataBase

import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import java.io.Serializable

data class MenuImages(
    var upImage: CircleMenuImage,
    var downImage: CircleMenuImage,
    var rightImage: CircleMenuImage,
    var leftImage: CircleMenuImage,
) : Serializable {
    companion object {
        fun initial() = MenuImages(
            upImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Error
            ),
            downImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Error
            ),
            rightImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Error
            ),
            leftImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Error
            ),
        )
    }
}
