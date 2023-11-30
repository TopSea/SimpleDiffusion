package top.topsea.simplediffusion.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.util.Calendar
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import top.topsea.simplediffusion.api.dto.QueueData
import top.topsea.simplediffusion.data.param.ImageData
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.sql.Date
import java.util.Base64
import java.util.UUID


@Keep
enum class DeleteImage {
    ONE,
    THREE,
    WEEK,
    WEEK2,
    MONTH,
    MONTH6,
    NEVER,
}

object FileUtil {

    fun innerImg2Base64(imageName: String, context: Context): String {
        val result: String
        val file = File(context.filesDir, imageName)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val outputStream = ByteArrayOutputStream()
        //把bitmap100%高质量压缩 到 output对象里
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val bytes: ByteArray = outputStream.toByteArray()
        outputStream.close()
        result = Base64.getEncoder().encodeToString(bytes)
        return result
    }

    fun imageName2Base64(context: Context,imageName: String): String {
        val image = File(context.filesDir, imageName)
        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
        return bitmap2Base64(bitmap)
    }

    fun bitmap2Base64(bitmap: Bitmap): String {
        val result: String
        val outputStream = ByteArrayOutputStream()
        //把bitmap100%高质量压缩 到 output对象里
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val bytes: ByteArray = outputStream.toByteArray()
        outputStream.close()
        result = Base64.getEncoder().encodeToString(bytes)
        return result
    }

    fun saveBase64Images(base64s: Array<String>?, context: Context, info: String?): List<ImageData?>? {
        if (!base64s.isNullOrEmpty()) {
            val images = ArrayList<ImageData?>(base64s.size)
            base64s.forEach {
                images.add(saveBase64Image(it, context, TextUtil.toPrintJsonView(info!!)))
            }
            return images
        }
        return null
    }

    fun saveBase64Image(base64Code: String, context: Context, info: String = ""): ImageData? {
        val buffer: ByteArray = Base64.getDecoder().decode(base64Code)
        try {
            val imageName = "${System.currentTimeMillis()}.png"
            context.openFileOutput(imageName, Context.MODE_PRIVATE).use { fos ->
                fos.write(buffer)
            }
            return ImageData(imageName = imageName, info = info, genDate = Date(System.currentTimeMillis()))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun splitImages(srcData: QueueData, imgSize: Size, context: Context, batchSize: Int = 1): List<ImageData> {
        val base64 = srcData.image.split(",")
        val byteArray: ByteArray = Base64.getDecoder().decode(base64[1])
        val images: MutableList<ImageData> = mutableListOf()
        TextUtil.topsea("splitImages....", Log.ERROR)

        try {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            val x = bitmap.width / imgSize.width
            val y = bitmap.height / imgSize.height

            var batch = 0

            for(i in 0 until  x) {
                for (j in 0 until y) {
                    val bmp = Bitmap.createBitmap(bitmap, i*imgSize.width, j*imgSize.height, imgSize.width, imgSize.height)
                    val imageName = "${System.currentTimeMillis()}.png"
                    context.openFileOutput(imageName, Context.MODE_PRIVATE).use { fos ->
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    }
                    images.add(
                        ImageData(imageName = imageName, info = srcData.infotext, genDate = Date(System.currentTimeMillis()))
                    )
                    batch++
                    if (batch == batchSize)
                        break
                }
                if (batch == batchSize)
                    break
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return images
    }

    fun saveQueueImage(srcData: QueueData, context: Context): ImageData? {
        TextUtil.topsea("saveQueueImage....", Log.DEBUG)

        val base64 = srcData.image.split(",")
        val byteArray: ByteArray = Base64.getDecoder().decode(base64[1])

        try {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            val imageName = "${System.currentTimeMillis()}.png"
            context.openFileOutput(imageName, Context.MODE_PRIVATE).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            return ImageData(imageName = imageName, info = srcData.infotext, genDate = Date(System.currentTimeMillis()))

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getIBFromBase64(base64Str: String): ImageBitmap? {
        val byteArray: ByteArray = Base64.getDecoder().decode(base64Str)
        try {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return bitmap.asImageBitmap()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getAppSpecificAlbumStorageDir(context: Context, albumName: String): File {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(context.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES), albumName)
        if (!file.mkdirs()) {
            TextUtil.topsea("SpecificAlbum Directory not created")
        }
        return file
    }

    fun deleteImage(imageName: String, context: Context) {
        val image = File(context.filesDir, imageName)
        if (image.exists()) {
            image.delete()
        }
    }

    suspend fun deleteImageByDate(images: List<String>, context: Context) {
        images.forEach {
            val image = File(context.filesDir, it)
            if (image.exists()) {
                image.delete()
            }
        }
    }

    fun getImageBitmap(fileName: String, context: Context): ImageBitmap {
        val file = File(context.filesDir, fileName)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        return bitmap.asImageBitmap()
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, fileName: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                val fat = if (fileName.contains("png")) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                bitmap.compress(fat, 100, fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun saveFile(context: Context, input: InputStream, fileName: String, saveProgress: (Long) -> Unit) {
        try {
            val data = ByteArray(Constant.download_buffer)
            var len: Int
            var currentLength: Long = 0

            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                while (input.read(data, 0, Constant.download_buffer).also { len = it } != -1) {
                    fos.write(data, 0, len)
                    currentLength += len
                    // 计算当前下载进度
                    saveProgress(100 * currentLength)
                }
//                fos.write(input.readBytes())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(tmpImg: File): Float {
        var degree = 0f
        try {
            val exifInterface = ExifInterface(tmpImg)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270f
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }


    /*
    * 旋转图片
    * @param angle
    * @param bitmap
    * @return Bitmap
    */
    fun rotateImage(angle: Float, context: Context, imageName: String, isFace: Boolean) {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle)
        if (isFace) {
            matrix.postScale(-1f, 1f)
        }

        val path = File(context.filesDir, imageName).absolutePath

        val bitmap = BitmapFactory.decodeFile(path)
        // 创建新的图片
        val rotateImg = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        context.openFileOutput(imageName, Context.MODE_PRIVATE).use { fos ->
            rotateImg.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
    }

    fun checkMD5(file: File, md5: String): Boolean {
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val md = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(8192)
            var length: Int
            while (fis.read(buffer).also { length = it } != -1) {
                md.update(buffer, 0, length)
            }
            val md5Bytes = md.digest()
            val sb = StringBuilder()
            for (md5Byte in md5Bytes) {
                val hex = Integer.toHexString(0xff and md5Byte.toInt())
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }
            val md5Str = sb.toString()
            TextUtil.topsea("md5: $md5Str, ${md5Str == md5}")
            return md5Str == md5
        } catch (e:Exception) {
            e.printStackTrace()
        } finally {
            try {
                fis?.close()
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    suspend fun shareImage(imageName: String, context: Context) {
        val image = File(context.filesDir, imageName)
        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        val uri = Uri.parse(
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "IMG" + Calendar.getInstance().time,
                null
            )
        )
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, "title"))
    }


    // TODO
    fun downloadImageToAlbum(image: ImageData, context: Context): Boolean {
        val imageFile = File(context.filesDir, image.imageName)
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            return saveImg2AlbumAQ(bitmap, context, image)
        } else {
            return saveImg2AlbumBQ(bitmap, context, image)
        }
    }

    fun writeUUID2File(context: Context): String {
        val filename = "pwd"
        val fileContents = UUID.randomUUID().toString()
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
        return fileContents
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun saveImg2AlbumAQ(bitmap: Bitmap, context: Context, image: ImageData): Boolean {
        val contentValues = getImgContentValues(context, image)
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false

        val os: OutputStream? = context.contentResolver.openOutputStream(uri)
        try {
            if (os != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
                return true
            }
        } catch (e: Exception) {
            context.contentResolver.delete(uri, null, null)
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            }  catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun saveImg2AlbumBQ(bitmap: Bitmap, context: Context, image: ImageData): Boolean {
        val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val imageFile = File(picDir, context.packageName + File.separator + image.imageName)

        if (!imageFile.exists()) {
            imageFile.parentFile?.mkdirs()
            imageFile.createNewFile()
        }
        val os = BufferedOutputStream(FileOutputStream(imageFile))

        try {
            val result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            if (!bitmap.isRecycled) bitmap.recycle()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os.close()
            }  catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun getImgContentValues(context: Context, image: ImageData): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, image.imageName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + context.packageName)
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, image.genDate.time)
        contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, image.genDate.time)
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        return contentValues
    }
}