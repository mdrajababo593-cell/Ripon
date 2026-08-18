package com.example.storage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.File

object StorageManager {

    const val LOCAL_CONFIG_JSON = "{\n  \"serverLoginUrl\": \"http://127.0.0.1:6677/\"\n}"

    private val TARGET_DIRS = listOf(
        "/storage/emulated/0/Android/data/com.dts.freefireth/files",
        "/storage/emulated/0/Android/data/com.dts.freefiremax/files",
        "/sdcard/Android/data/com.dts.freefireth/files",
        "/sdcard/Android/data/com.dts.freefiremax/files"
    )

    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val read = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            val write = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            read && write
        }
    }

    fun getStoragePermissionIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:" + context.packageName)
                }
            } catch (_: Exception) {
                Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:" + context.packageName)
            }
        }
    }

    data class DeployResult(
        val totalAttempted: Int,
        val deployedPaths: List<String>,
        val failedPaths: List<String>
    )

    /**
     * Deploys localconfig.json to target Free Fire directory paths.
     */
    fun deployLocalConfig(context: Context): DeployResult {
        val deployed = mutableListOf<String>()
        val failed = mutableListOf<String>()

        for (dirPath in TARGET_DIRS) {
            try {
                val dir = File(dirPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val configFile = File(dir, "localconfig.json")
                configFile.writeText(LOCAL_CONFIG_JSON, Charsets.UTF_8)
                deployed.add(configFile.absolutePath)
            } catch (e: Exception) {
                failed.add("$dirPath (${e.localizedMessage ?: "Access Denied"})")
            }
        }

        // Also save in app internal / external fallback
        try {
            val appFile = File(context.filesDir, "localconfig.json")
            appFile.writeText(LOCAL_CONFIG_JSON, Charsets.UTF_8)
            deployed.add(appFile.absolutePath)
        } catch (_: Exception) {}

        return DeployResult(
            totalAttempted = TARGET_DIRS.size + 1,
            deployedPaths = deployed,
            failedPaths = failed
        )
    }

    /**
     * Cleans up and deletes localconfig.json across all known paths to restore normal game behavior.
     */
    fun cleanupLocalConfig(context: Context): Int {
        var deletedCount = 0
        for (dirPath in TARGET_DIRS) {
            try {
                val file = File(dirPath, "localconfig.json")
                if (file.exists() && file.delete()) {
                    deletedCount++
                }
            } catch (_: Exception) {}
        }
        try {
            val appFile = File(context.filesDir, "localconfig.json")
            if (appFile.exists() && appFile.delete()) {
                deletedCount++
            }
        } catch (_: Exception) {}
        return deletedCount
    }

    /**
     * Creates a sharable localconfig.json file in app cache/downloads for manual ZArchiver placement.
     */
    fun exportLocalConfigFile(context: Context): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, "localconfig.json")
            file.writeText(LOCAL_CONFIG_JSON, Charsets.UTF_8)
            file
        } catch (_: Exception) {
            null
        }
    }
}
