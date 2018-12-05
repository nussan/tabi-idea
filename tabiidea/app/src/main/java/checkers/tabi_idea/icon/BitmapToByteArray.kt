package checkers.tabi_idea.icon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class BitmapToByteArray {

    fun toByteArray(bmp : Bitmap,quality: Int) : ByteArray{
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        return baos.toByteArray()
    }

    fun stringToBitmap(bmpStr : String ,quality:Int) : Bitmap {
        val baStrArr = bmpStr.split(Regex(", |\\[|]"),0)
        var baBytArr = ByteArray(baStrArr.size-2)
        for(i in 1 .. baStrArr.size-2) {
            var tmp = baStrArr[i].toInt()
            if(tmp >127) tmp -= 256
            baBytArr[i-1] = tmp.toByte()
        }
        val options : BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = quality
        return BitmapFactory.decodeByteArray(baBytArr,0,baBytArr.size,options)

    }
}