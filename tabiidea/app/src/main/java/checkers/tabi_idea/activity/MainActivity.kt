package checkers.tabi_idea.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.Installation
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.CategoryListFragment
import checkers.tabi_idea.fragment.EventListFragment
import checkers.tabi_idea.provider.Repository
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity(),
        ColorPickerDialogListener {

    private val repository = Repository()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //sIdを取得
        val uuid = Installation.id(this)

        if (savedInstanceState == null) {
            Log.d("editprob", uuid)
            repository.getUser(uuid) {
                Log.d("usertoken", it.token)
                if (it.id == -1) {
                    Log.d("editprob", "X")
                    val newUser = mapOf(
                            "uuid" to uuid,
                            "name" to "新しいユーザー"
                    )
                    repository.addUser(newUser) { user: User ->
                        this.user = user
                        user.token = "Token " + user.token
                        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                        val baos = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG,100,baos)
                        val bmparr = baos.toByteArray();
                        //TODO ユーザーアイコン初期セット
                        repository.setUserIcon(bmparr,user.id,user.token){
                            val drw = BitmapDrawable(it)
                            supportActionBar?.setIcon(drw)
                        }
                        repository.getEventList(user.token, user.id) {
                            toEventListFragment(user, it)
                        }
                    }
                } else {
                    Log.d("editprob", "O")
                    Log.d("usertoken", it.token)
                    it.token = "Token " + it.token
                    repository.getEventList(it.token, it.id) { evel: MutableList<Event> ->
                        toEventListFragment(it, evel)
                    }
                }
            }
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    private fun toEventListFragment(user: User, eventList: MutableList<Event>) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, EventListFragment.newInstance(user, eventList))
                .commit()
    }

    //mainactibityにメニュー追加する
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return true
    }*/

    override fun onDialogDismissed(dialogId: Int) {
        Log.d(TAG, "onDialogDismissed() called with: dialogId = [$dialogId]")
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        Log.d(TAG, "onColorSelected() called with: dialogId = [$dialogId], color = [$color]")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        when (dialogId) {
            DIALOG_ID -> {
                val color = Integer.toHexString(color).toUpperCase().substring(2)
                (currentFragment as? CategoryListFragment)?.changeColor("#$color")

            }
        }
    }


    companion object {
        private const val DIALOG_ID = 0
        private const val TAG = "MainActivity"
    }
}
