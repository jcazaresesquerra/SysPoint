package com.app.syspoint.interactor.installer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.app.syspoint.BuildConfig
import timber.log.Timber
import java.io.File

const val TAG = "ApkInstaller"

class ApkInstaller {
    fun installApplicationFromCpanel(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uri, "application/vnd.android.package-archive"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Timber.tag(TAG).e( e, "Error in opening the file!" )
        }
    }

    fun installApplicationFromFireBase(context: Context, file: File) {
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(
                    context,
                    file
                ), "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Timber.tag(TAG).e(e, "Error in opening the file!")
            }
        } else {
            Toast.makeText(
                context,
                "installing",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun uriFromFile(context: Context, file: File): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID.toString() + ".provider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }
}