package checkers.tabi_idea.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.fragment.OwnPageFragment
import checkers.tabi_idea.R
import checkers.tabi_idea.data.User
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
        if (savedInstanceState == null)
            setUserInf()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        layoutWidth = container.width.toFloat()
        layoutHeight = container.height.toFloat()
//        Toast.makeText(this, "${container?.width}, ${container?.height}", Toast.LENGTH_SHORT).show()
    }
    private fun toOwnPageFragment(user:User) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, OwnPageFragment.newInstance(user))
//             初期状態のため戻るボタンで戻らない   .addToBackStack(null)
                .commit()
    }
    fun setUserInf(){
        repository.getUser { it ->
            toOwnPageFragment(it)
        }
    }
}
