package com.codequest.learn.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.codequest.learn.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val releaseNotes: String,
    val apkUrl: String,
    val isMandatory: Boolean = false
)

class AppUpdateManager {

    private val gson = Gson()

    /**
     * Checks a remote version endpoint (e.g. GitHub Releases, custom raw JSON, or cloud server).
     * Returns AppUpdateInfo if a newer versionCode is available, null otherwise.
     */
    suspend fun checkForUpdates(
        manifestUrl: String = DEFAULT_UPDATE_MANIFEST_URL
    ): Result<AppUpdateInfo?> = withContext(Dispatchers.IO) {
        try {
            val url = URL(manifestUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val json = connection.inputStream.bufferedReader().use { it.readText() }
                val updateInfo = gson.fromJson(json, AppUpdateInfo::class.java)

                if (updateInfo.versionCode > BuildConfig.VERSION_CODE) {
                    Result.success(updateInfo)
                } else {
                    Result.success(null) // Already up to date
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun openConnectionWithRedirects(initialUrl: String): HttpURLConnection {
        var currentUrl = initialUrl
        var redirectCount = 0
        while (redirectCount < 6) {
            val url = URL(currentUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = 15000
                readTimeout = 30000
                setRequestProperty("User-Agent", "CodeQuest-Updater")
            }
            connection.connect()

            val status = connection.responseCode
            if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == HttpURLConnection.HTTP_MOVED_PERM ||
                status == HttpURLConnection.HTTP_SEE_OTHER ||
                status == 307 || status == 308
            ) {
                val newUrl = connection.getHeaderField("Location")
                connection.disconnect()
                if (newUrl == null) break
                currentUrl = if (newUrl.startsWith("http")) newUrl else URL(url, newUrl).toString()
                redirectCount++
            } else {
                return connection
            }
        }
        val url = URL(currentUrl)
        return (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 30000
            setRequestProperty("User-Agent", "CodeQuest-Updater")
        }
    }

    /**
     * Downloads the APK file with real-time progress and launches the native package installer
     */
    suspend fun downloadAndInstallApk(
        context: Context,
        updateInfo: AppUpdateInfo,
        onProgress: (Float) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val destinationFile = File(context.cacheDir, "CodeQuest_v${updateInfo.versionName}.apk")
            if (destinationFile.exists()) destinationFile.delete()

            val connection = openConnectionWithRedirects(updateInfo.apkUrl)
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Server returned HTTP ${connection.responseCode}: ${connection.responseMessage}")
            }

            val fileLength = connection.contentLength
            val input = connection.inputStream
            val output = FileOutputStream(destinationFile)

            val buffer = ByteArray(8192)
            var total: Long = 0
            var count: Int

            while (input.read(buffer).also { count = it } != -1) {
                total += count.toLong()
                if (fileLength > 0) {
                    val progress = (total.toFloat() / fileLength.toFloat()).coerceIn(0f, 1f)
                    withContext(Dispatchers.Main) {
                        onProgress(progress)
                    }
                }
                output.write(buffer, 0, count)
            }

            output.flush()
            output.close()
            input.close()
            connection.disconnect()

            withContext(Dispatchers.Main) {
                onProgress(1f)
                installApk(context, destinationFile, updateInfo)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Triggers the Android OS Package Installer prompt to update the app in-place
     */
    fun installApk(context: Context, apkFile: File, updateInfo: AppUpdateInfo) {
        if (!apkFile.exists() || apkFile.length() < 1000) {
            Toast.makeText(context, "Downloaded update is incomplete. Please try again.", Toast.LENGTH_LONG).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                Toast.makeText(
                    context,
                    "Please allow CodeQuest to install apps from this source",
                    Toast.LENGTH_LONG
                ).show()
                val permissionIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(permissionIntent)
                return
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Show update notification as well
        showUpdateNotification(context, installIntent, updateInfo.versionName)

        context.startActivity(installIntent)
    }

    private fun showUpdateNotification(context: Context, installIntent: Intent, versionName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "codequest_app_updates"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for CodeQuest app updates"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            installIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("CodeQuest v$versionName Ready")
            .setContentText("Tap to install the update now.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    companion object {
        // Fallback or customizable update manifest URL (e.g. GitHub raw URL)
        const val DEFAULT_UPDATE_MANIFEST_URL = "https://raw.githubusercontent.com/Vinoth-46/CodeQuest/main/version.json"
    }
}
