package checkers.tabi_idea.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Installation
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.OwnPageFragment
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val repository = Repository()
    var layoutWidth = 0f
    var layoutHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //sIdを取得
        val uuid = Installation.id(this)

        if( savedInstanceState == null)
            Log.d("uuid" , uuid)
            repository.getUser(uuid) {
                if(it.id == -1){
                    val newuser = mapOf(
                            "id" to "uuid",
                            "name" to "新しいユーザー"
                    )
                    repository.addUser(newuser){
                        toOwnPageFragment(it)
                    }
                }else{
                    toOwnPageFragment(it)
                }
            }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        Log.d(this.javaClass.simpleName, "onWindowFocusChanged")
        super.onWindowFocusChanged(hasFocus)
        layoutWidth = container.width.toFloat()
        layoutHeight = container.height.toFloat()

        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if ( currentFragment is IOnFocusListenable) {
            currentFragment.onWindowFocusChanged(hasFocus)
        }
    }

    private fun toOwnPageFragment(user: User) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, OwnPageFragment.newInstance(user))
//             初期状態のため戻るボタンで戻らない   .addToBackStack(null)
                .commit()
    }

    interface IOnFocusListenable {
        fun onWindowFocusChanged(hasFocus: Boolean)
    }

}
