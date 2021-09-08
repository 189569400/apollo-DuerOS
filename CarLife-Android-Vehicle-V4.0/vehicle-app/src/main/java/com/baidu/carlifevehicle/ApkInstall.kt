package com.baidu.carlifevehicle

import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Build
import androidx.core.content.FileProvider
import java.io.File


object ApkInstall{
    fun installApk(context: Context, filePath: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //6.0及以下安装
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                Uri.parse(filePath),
                "application/vnd.android.package-archive"
            )
            //为这个新apk开启一个新的activity栈
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //开始安装
            context.startActivity(intent)
        } else {
            //7.0及以上
            val file = File(filePath)
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            val apkUri: Uri = FileProvider.getUriForFile(context, "com.baidu.downloadmanager", file)
            val intent = Intent(Intent.ACTION_VIEW)
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            context.startActivity(intent)
        }
    }

}