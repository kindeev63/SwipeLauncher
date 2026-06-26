package com.kindeev.swipelauncher.data.coil

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import androidx.core.net.toUri

class AppIconFetcher(
    private val context: Context,
    private val data: String
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        return try {
            val drawable = context.packageManager.getApplicationIcon(data)
            DrawableResult(
                drawable = drawable,
                isSampled = false,
                dataSource = DataSource.DISK
            )
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }

    class Factory(private val context: Context) : Fetcher.Factory<Uri> {

        override fun create(
            data: Uri,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            return if (data.scheme == SCHEME_APP_ICON) {
                val packageName = data.host
                if (packageName != null) {
                    AppIconFetcher(context, packageName)
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