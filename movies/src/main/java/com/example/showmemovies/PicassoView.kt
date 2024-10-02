package  com.example.showmemovies

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.composepicasso.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


@Composable
fun PicassoView(
    modifier: Modifier = Modifier,
    contentDescription: String?,
    model: String
) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    Log.d("PicassoView", if (imageBitmap != null) "Bitmap in memory" else "Bitmap NOT in memory")
    if (imageBitmap == null) {
        val httpClient = remember { okHttpClient }
        httpClient.newCall(request = Request.Builder().url(model).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.localizedMessage?.let { Log.d("PicassoView", it) }
                }

                override fun onResponse(call: Call, response: Response) {
                    val bitmap = BitmapFactory.decodeStream(response.body?.byteStream())
                    bitmap?.let {
                        Log.d("PicassoView", "Size -> W:${it.width} H:${it.height}")
                        imageBitmap = it
                    }
                }

            })
    }
    if (imageBitmap == null) return Image(
        painter = painterResource(id = R.drawable.ic_tostones),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    ) else Image(
        painter = BitmapPainter(image = imageBitmap!!.asImageBitmap()),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}


private val okHttpClient = OkHttpClient.Builder().build()