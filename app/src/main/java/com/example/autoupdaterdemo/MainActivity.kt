package com.example.autoupdaterdemo

import android.app.DownloadManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.domain.DownloadInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit var fileName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileName = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/a.apk"

        button.setOnClickListener {
            download("https://github.com/ConstantineMars/AutoUpdaterDemo/raw/master/apk/1.4.apk",
                 fileName )
        }

        button2.setOnClickListener {
            install(this, getApplicationContext().getPackageName(), fileName)
        }
    }

    fun removeAdmin() {
        val cn = ComponentName(packageName, packageName + ".AdminReceiver")
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        dpm.removeActiveAdmin(cn)
    }

        fun install(context: Context, packageName: String, apkPath: String) {

            // PackageManager provides an instance of PackageInstaller
            val packageInstaller = context.packageManager.packageInstaller

            // Prepare params for installing one APK file with MODE_FULL_INSTALL
            // We could use MODE_INHERIT_EXISTING to install multiple split APKs
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            params.setAppPackageName(packageName)

            // Get a PackageInstaller.Session for performing the actual update
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            // Copy APK file bytes into OutputStream provided by install Session
            val out = session.openWrite(packageName, 0, -1)
            val fis = File(apkPath).inputStream()
            fis.copyTo(out)
            session.fsync(out)
            out.close()

            // The app gets killed after installation session commit
            session.commit(
                PendingIntent.getBroadcast(context, sessionId,
                Intent("android.intent.action.MAIN"), 0).intentSender)
        }


//    fun download(link: String, path: String) {
//        Thread {
//            URL(link).openStream().use { input ->
//                FileOutputStream(File(path)).use { output ->
//                    input.copyTo(output)
//                    Snackbar.make(findViewById(R.id.content), "Done", Snackbar.LENGTH_LONG)
//                }
//            }
//
//    //            runOnUiThread({
//    //                //Update UI
//    //            })
//        }.start()
//
//
//    }

    fun downloadFile(
        url: String,
        file: String
//        title: String,
//        branch: String
    ) {
        val Download_Uri: Uri = Uri.parse(url)
        val downloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Download_Uri)
        request.setMimeType("application/vnd.android.package-archive");

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false)
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle("Downloading")
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription("Downloading File")
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            file
        )

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //Enqueue a new download and same the referenceId
        downloadManager .enqueue(request)
    }

    fun download(url: String, file: String) {
        val downloadManager = DownloadService.getDownloadManager(getApplicationContext());
        val downloadInfo = DownloadInfo.Builder().setUrl(url)
            .setPath(file)
            .build()
        downloadManager.download(downloadInfo);
    }

//    private fun startDownload(
//        downloadPath: String,
//        destinationPath: String
//    ) {
//        val uri =
//            Uri.parse(downloadPath) // Path where you want to download file.
//        val request = DownloadManager.Request(uri)
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
//        request.setTitle("Downloading a file") // Title for notification.
//        request.setVisibleInDownloadsUi(true)
//        request.setMimeType("application/vnd.android.package-archive");
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,destinationPath)
//
////        request.setDestinationInExternalPublicDir(
////            destinationPath,
////            uri.lastPathSegment
////        ) // Storage directory path
//        (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
//            request
//        ) // This will start downloading
//    }

}
