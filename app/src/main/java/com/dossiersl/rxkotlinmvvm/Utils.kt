package com.dossiersl.rxkotlinmvvm

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

class Utils {
    companion object
    {
        fun checkPermissions(ctx: Context, permission: String): Boolean {

            val permission = ContextCompat.checkSelfPermission(ctx, permission)

            return permission == PackageManager.PERMISSION_GRANTED


        }
    }
}