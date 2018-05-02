package com.obrusvit.filestoringbasics

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val defaultFileName = "my_file"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * External storage (mountable storage, not necessarily SD card)
     * Creates private file in the Android/data/[package name]/
     * You can create some default subdirectories using Environment
     * or get root file of your package by calling getExternalFilesDir(null)
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createPrivateFile(view: View) {

        if (isExternalStorageWritable()) {
            val fileName: String = getInputText()


            /*The following line creates Downloads folder. This is deleted when user uninstalls the app*/
            val file = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

            /*now I can write to file*/
            FileOutputStream(file).use {
                it.write("HelloWorld".toByteArray())
            }

            toast(applicationContext, "Private file created")

            et_file_name.text.clear()

        }
    }


    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: Int = 32

    /**
     * External storage (mountable storage, not necessarily SD card)
     * Creates file which can be permanently stored in the device and does not get deleted if you
     * uninstall the app.
     * However, we need to explicitly ask for permission to do this.
     */
    fun createPublicFile(view: View) {
        if (isExternalStorageWritable()) {

            //We need to have the permission to use public files explicitly
            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL)
            } else {
                //Permission has been granted already
                val fileName: String = getInputText()
                withPermissionNowCreateThatPublicFile(fileName)
            }
        }
    }

    /**
     * We obtained the permission from the user to work with files.
     * Now we can freely create permanent files.
     */
    private fun withPermissionNowCreateThatPublicFile(fileName: String) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            FileOutputStream(file).use {
                it.write("Hello, this was added while playing in my app to create files.".toByteArray())
            }

            toast(applicationContext, "Public file created id Downloads")
        } catch (e: Exception) {
            e.printStackTrace()
            toast(applicationContext, "Not created")
        }

        et_file_name.text.clear()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val filename =  getInputText()
                    withPermissionNowCreateThatPublicFile(filename)

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getInputText(): String {
        return if (et_file_name.text.isNotEmpty()) {
            et_file_name.text.toString()
        } else {
            defaultFileName
        }
    }
}


/** Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

/** Checks if external storage is available to at least read */
fun isExternalStorageReadable(): Boolean {
    return Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}


fun toast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}