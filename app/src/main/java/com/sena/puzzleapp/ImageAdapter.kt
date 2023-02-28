package com.sena.puzzleapp

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.io.IOException

class ImageAdapter(private val mContext: Context): BaseAdapter() {
    val am:AssetManager
    private var files: Array<String>? = null

    override fun getCount(): Int= files!!.size



    override fun getItemId(position: Int): Long =0



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.grid_element, null)
        val imageView = v.findViewById<ImageView>(R.id.gridImageView)
        imageView.post {
            object : AsyncTask<Any?, Any? , Any>(){
                private var bitmap :Bitmap? = null
                override fun doInBackground(vararg poition: Any?): Any? {
                    bitmap = getPicFromAsset(imageView, files!![position])
                    return null

                }

                override fun onPostExecute(result: Any?) {
                    super.onPostExecute(result)
                    imageView.setImageBitmap(bitmap)
                }
            }.execute()

        }
        return v
    }
    private fun getPicFromAsset(imageView: ImageView?, assetName: String): Bitmap? {
        val targetW = imageView!!.width
        val targetH = imageView!!.height

        return if (targetW==0 || targetH==0){
            //wiew has no dimension set
            null
        } else try {
            val `is` = am.open("img/$assetName")
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`is`, Rect(-1, -1, -1, -1), bmOptions)

            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            val scaleFactor = Math.min(
                photoW / targetW / photoH, targetH
            )
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true
            BitmapFactory.decodeStream(
                `is`,
                Rect(-1, -1, -1, -1), bmOptions
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }


    }
    init {
        am= mContext.assets
        try {
            files = am.list("img")
        }catch (e: IOException){
            e.printStackTrace()
        }

    }

}




