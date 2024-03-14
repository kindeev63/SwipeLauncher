package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import com.kindeev.swipelauncher.domain.DataObject
import com.kindeev.swipelauncher.domain.DataObject.getAs
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

class CheckCircleMenuUseCase(
    private val mainAppVM: MainAppVM,
    private val context: Context,
    ) {
    fun invoke(
        circleMenu: CircleMenu
    ): Boolean {

        // Check Actions

        // Check OpenApp
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.upAction.data.getAs(OpenApp::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.downAction.data.getAs(OpenApp::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.rightAction.data.getAs(OpenApp::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.leftAction.data.getAs(OpenApp::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(openApp.packageName, context)) return false
        }

        // Check OpenCircleMenu
        val allCircleMenuIds = mainAppVM.allCircleMenu.value?.map { it.id } ?: emptyList()
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.upAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.downAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.rightAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.leftAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }


        // Check Images

        // Check AppImages
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.upImage.data.getAs(AppImage::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.downImage.data.getAs(AppImage::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.rightImage.data.getAs(AppImage::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.leftImage.data.getAs(AppImage::class.java)
            if (!DataObject.AppDataObject.isAppInstalled(appImage.packageName, context)) return false
        }

        // Check UserImages
        val fileNames = context.filesDir.listFiles()?.map { it.name } ?: emptyList()
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.upImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.downImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.rightImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.leftImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        return true
    }
}