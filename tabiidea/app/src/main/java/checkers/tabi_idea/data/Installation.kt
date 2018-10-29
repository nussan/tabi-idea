package checkers.tabi_idea.data

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*


object Installation {
    private var sID: String? = null
    private val INSTALLATION = "INSTALLATION"

    @Synchronized
    fun id(context: Context): String {
        if (sID == null) {
            val installation = File(context.getFilesDir(), INSTALLATION)
            try {
                if (!installation.exists())
                    writeInstallationFile(installation)
                sID = readInstallationFile(installation)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
        return sID!!
    }

    @Throws(IOException::class)
    private fun readInstallationFile(installation: File): String {
        val f = RandomAccessFile(installation, "r")
        val bytes = ByteArray(f.length().toInt())
        f.readFully(bytes)
        f.close()
        return String(bytes)
    }

    @Throws(IOException::class)
    private fun writeInstallationFile(installation: File) {
        val out = FileOutputStream(installation)
        val id = UUID.randomUUID().toString()
        out.write(id.toByteArray())
        out.close()
    }
}