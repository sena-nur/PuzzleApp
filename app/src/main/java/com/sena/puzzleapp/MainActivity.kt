package com.sena.puzzleapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FilePermission
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var mCurrenPhotoPath : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val am = assets
        try{
            val files = am.list("img")
            val grid= findViewById<GridView>(R.id.grid)

            grid.adapter = ImageAdapter(this@MainActivity)
            AdapterView
                .OnItemClickListener{ adapterView, view, i ,l ->
                    val intent = Intent(applicationContext , PuzzleActivity::class.java)
                    intent.putExtra("assetName", files!! [i% files.size])
                    startActivity(intent)
                }.also { grid.onItemClickListener = it }
        }
        catch (e: IOException) {
            Toast.makeText(this,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }

        }



    fun onImageCameraClicked (view: android.view.View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null ) {
            var photoFile : File? = null
            try {
                photoFile = createImageFile()
            }catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, e.message,Toast.LENGTH_LONG).show()
            }
            if(photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,applicationContext.packageName + ".fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_IMAGE_GAPTURE)
            }

        }
    }

    fun onImageGalleryClicked( view : android.view.View) {
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this , arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE ,
                ) , REQUEST_PERMISSION_WRITE_READ_EXTERNAL_STORAGE
            )
        }
        else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent , REQUEST_IMAGE_GALLERY)
        }







    }

    @Throws(IOException::class)
    private  fun createImageFile():File? {
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted initiate request

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)

        }
        else {
            //create an image file name
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_$timestamp"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
            )
            mCurrenPhotoPath = image.absolutePath // save this to use in the intent

            return image

        }
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if(grantResults.size >0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onImageCameraClicked(View(this))
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_IMAGE_GAPTURE && requestCode == RESULT_OK) {
            val intent = Intent (this, PuzzleActivity ::class.java)
            intent.putExtra("mCurrentPhotoPath" , mCurrenPhotoPath)
            startActivity(intent)

        }
        if(requestCode == REQUEST_IMAGE_GALLERY && requestCode == RESULT_OK) {
            val uri = data!!.data
            intent.putExtra("mCurrentPhotoUri", uri)
            startActivity(intent)
        }
    }



    companion object {
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE =2
        private const val REQUEST_IMAGE_GAPTURE=1

        const val REQUEST_PERMISSION_WRITE_READ_EXTERNAL_STORAGE =3

        const val REQUEST_IMAGE_GALLERY =4




    }

}