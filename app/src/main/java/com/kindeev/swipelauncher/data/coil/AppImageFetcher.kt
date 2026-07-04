package com.kindeev.swipelauncher.data.coil

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import androidx.core.net.toUri
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options

class AppIconFetcher(
    private val launcherApps: LauncherApps,
    private val context: Context,
    private val packageName: String
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        return try {
            val activities = launcherApps.getActivityList(packageName, Process.myUserHandle())
            val activityInfo = activities.firstOrNull()

            val drawable = if (activityInfo != null) {
                val dpi = context.resources.displayMetrics.densityDpi
                activityInfo.getIcon(dpi)
            } else {
                context.packageManager.getApplicationIcon(packageName)
            }

            DrawableResult(
                drawable = drawable,
                isSampled = true,
                dataSource = DataSource.MEMORY
            )
        } catch (_: PackageManager.NameNotFoundException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    class Factory(
        private val context: Context,
        private val launcherApps: LauncherApps
    ) : Fetcher.Factory<Uri> {

        override fun create(
            data: Uri,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            return if (data.scheme == SCHEME_APP_ICON) {
                val packageName = data.host
                if (packageName != null) {
                    AppIconFetcher(launcherApps, context, packageName)
                } else {
                    null
                }
            } else {
                null
            }
        }

        companion object {
            const val SCHEME_APP_ICON = "app-icon"
        }
    }
}

fun appIconUri(packageName: String): Uri =
    "${AppIconFetcher.Factory.SCHEME_APP_ICON}://$packageName".toUri()