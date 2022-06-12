package com.dicoding.ripeai.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Color.GREEN
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ripeai.R
import com.dicoding.ripeai.databinding.ActivityUploadBinding
import com.dicoding.ripeai.ui.UserViewModelFactory
import com.dicoding.ripeai.ui.UtilsCamera
import io.grpc.ManagedChannel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class UploadActivity : AppCompatActivity() {

    val TAG = "TFServingDemo"
    val INPUT_IMG_HEIGHT = 150
    val INPUT_IMG_WIDTH = 150
    val TEST_IMG_NAME = "user.png"

    // This is for Android Emulator
    val SIGNATURE_NAME = "serving_default"

    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    private lateinit var client: OkHttpClient

    private val channel: ManagedChannel? = null


    private lateinit var binding: ActivityUploadBinding
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var token: String
    private var getFile: File? = null

    private var inputImgBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setuppermission()
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }


        val factory: UserViewModelFactory = UserViewModelFactory.getInstance(this)
        uploadViewModel = ViewModelProvider(this, factory)[UploadViewModel::class.java]

        val data = intent.getStringExtra(EXTRA_FRUIT)
        val ripe = "Ripe"

        createRESTRequest()

        binding.tvFruitName.text = "Fruit : $data"
        binding.tvPrediction.text = "ripeness : $ripe "
        binding.buttonCam.setOnClickListener { useCamera() }
        binding.buttonGallery.setOnClickListener { useGallery() }
        binding.buttonUpload.setOnClickListener { uploadPhoto() }


    }
    private fun createRESTRequest(): Request? {
        //Create the REST request.
        //Create the REST request.
        val INPUT_IMG_WIDTH = 150
        val INPUT_IMG_HEIGHT = 150
        val inputImg = IntArray(INPUT_IMG_HEIGHT * INPUT_IMG_WIDTH)
        val inputImgRGB =
            Array(1) {
                Array(INPUT_IMG_HEIGHT) {
                    Array(INPUT_IMG_WIDTH) {
                        IntArray(3)
                    }
                }
            }
        inputImgBitmap?.getPixels(
            inputImg,
            0,
            INPUT_IMG_WIDTH,
            0,
            0,
            INPUT_IMG_WIDTH,
            INPUT_IMG_HEIGHT
        )
        var pixel: Int
        for (i in 0 until INPUT_IMG_HEIGHT) {
            for (j in 0 until INPUT_IMG_WIDTH) {
                // Extract RBG values from each pixel; alpha is ignored
                pixel = inputImg[i * INPUT_IMG_WIDTH + j]/255
                inputImgRGB[0][i][j][0] = pixel
                inputImgRGB[0][i][j][1] = pixel
                inputImgRGB[0][i][j][2] = pixel
            }
        }

        val requestBody: RequestBody = RequestBody.Companion.create(
            "{\"signature_name\": \"serving_default\",\"instances\": " + inputImgRGB.contentDeepToString() + "}", JSON
        )

        return Request.Builder()
            .url("http://34.70.38.194:8501/v1/Models/Banana:predict")
            .post(requestBody)
            .build()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun uploadPhoto() {
        if(inputImgBitmap!=null){
            Log.d(TAG, "Uploading photo $inputImgBitmap")
            binding.imageView.setImageBitmap(inputImgBitmap)
            val request = createRESTRequest()
            try {
                client = OkHttpClient.Builder()
                    .connectTimeout(5000, TimeUnit.SECONDS)
                    .writeTimeout(5000, TimeUnit.SECONDS)
                    .readTimeout(5000, TimeUnit.SECONDS)
                    .callTimeout(5000, TimeUnit.SECONDS)
                    .build()
                val response: Response = client!!.newCall(request!!).execute()
                val responseObject = JSONObject(response.body?.string())
                postprocessRESTResponse(responseObject)
            } catch (e: IOException) {
                Log.e(TAG, e.message!!)
                return
            } catch (e: JSONException) {
                Log.e(TAG, e.message!!)
                return
            }

        }
    }

    private fun postprocessRESTResponse(responseObject: JSONObject) {
        // Process the REST response.
        // Process the REST response.
        val predictionsArray = responseObject.getJSONArray("predictions")
        binding.tvPrediction.text = predictionsArray.toString()
        //You only send one image, so you directly extract the first element.
        //You only send one image, so you directly extract the first element.
        val predictions = predictionsArray.getJSONObject(0)
        // Argmax
        // Argmax
        var maxIndex = 0
        val detectionScores = predictions.getJSONArray("detection_scores")
        for (j in 0 until predictions.getInt("num_detections")) {
            maxIndex =
                if (detectionScores.getDouble(j) > detectionScores.getDouble(maxIndex + 1)) j else maxIndex
        }
        val detectionClass = predictions.getJSONArray("detection_classes").getInt(maxIndex)
        val boundingBox = predictions.getJSONArray("detection_boxes").getJSONArray(maxIndex)
        val ymin = boundingBox.getDouble(0)
        val xmin = boundingBox.getDouble(1)
        val ymax = boundingBox.getDouble(2)
        val xmax = boundingBox.getDouble(3)
        displayResult(
            detectionClass,
            ymin.toFloat(),
            xmin.toFloat(),
            ymax.toFloat(),
            xmax.toFloat()
        )

    }
    private fun displayResult(
        detectionClass: Int,
        ymin: Float,
        xmin: Float,
        ymax: Float,
        xmax: Float
    ) {
        val left = xmin * INPUT_IMG_WIDTH
        val right = xmax * INPUT_IMG_WIDTH
        val top = ymin * INPUT_IMG_HEIGHT
        val bottom = ymax * INPUT_IMG_HEIGHT
        // Draw bounding box
        val resultInputBitmap = inputImgBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultInputBitmap)
        val paint = Paint()
        paint.setStyle(Paint.Style.STROKE)
        paint.setStrokeWidth(5F)
        paint.setColor(GREEN)
        canvas.drawRect(left, top, right, bottom, paint)
        binding.imageView.setImageBitmap(resultInputBitmap)
        binding.tvPrediction.setText("Predicted class: $detectionClass")
    }

    private fun useGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.pick))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val result = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImg)
            val myFile = UtilsCamera.uriToFile(selectedImg, this@UploadActivity)
            inputImgBitmap = result
            getFile = myFile
            binding.imageView.setImageBitmap(result)
        }
    }

    private fun useCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            val toFile = UtilsCamera.bitmapToFile(this@UploadActivity, result)
            getFile = toFile
            inputImgBitmap = result
            binding.imageView.setImageBitmap(result)
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }



    private fun setuppermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val EXTRA_FRUIT ="banana"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val CAMERA_X_RESULT = 200
    }

}

private fun RequestBody.Companion.create(s: String, json: MediaType) = s.toRequestBody(json)
