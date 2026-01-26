package com.fahim.shieldcheck.data.local.datasource

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager
) {

    fun getInstalledPackages(): List<PackageInfo> {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
        }

        return try {
            packageManager.getInstalledPackages(flags)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
            }
            packageManager.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getAppName(packageInfo: PackageInfo): String {
        return try {
            packageInfo.applicationInfo?.let {
                packageManager.getApplicationLabel(it).toString()
            } ?: packageInfo.packageName
        } catch (e: Exception) {
            packageInfo.packageName
        }
    }

    fun getAppIcon(packageInfo: PackageInfo): Drawable? {
        return try {
            packageInfo.applicationInfo?.loadIcon(packageManager)
        } catch (e: Exception) {
            null
        }
    }

    fun getVersionName(packageInfo: PackageInfo): String? {
        return packageInfo.versionName
    }

    fun getVersionCode(packageInfo: PackageInfo): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }

    fun isSystemApp(packageInfo: PackageInfo): Boolean {
        return packageInfo.applicationInfo?.let {
            (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } ?: false
    }

    fun getInstalledDate(packageInfo: PackageInfo): Date? {
        return try {
            Date(packageInfo.firstInstallTime)
        } catch (e: Exception) {
            null
        }
    }

    fun getLastUpdatedDate(packageInfo: PackageInfo): Date? {
        return try {
            Date(packageInfo.lastUpdateTime)
        } catch (e: Exception) {
            null
        }
    }

    fun getRequestedPermissions(packageInfo: PackageInfo): List<String> {
        return packageInfo.requestedPermissions?.toList() ?: emptyList()
    }

    fun isPermissionGranted(packageName: String, permission: String): Boolean {
        return try {
            packageManager.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
}
