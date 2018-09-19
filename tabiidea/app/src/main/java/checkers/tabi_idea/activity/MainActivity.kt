package checkers.tabi_idea.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.fragment.OwnPageFragment
import checkers.tabi_idea.R
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val user: User = User(
            0,
            "たきかわ",
            mutableListOf(
                    Event("研究室旅行", mutableListOf(
                            MindMapObject(0, "旅行", 1f / 2, 1f / 2, mutableListOf(1, 2, 3, 4)),
                            MindMapObject(1, "行先", 1f / 2, 1f / 4, mutableListOf(0)),
                            MindMapObject(2, "予算", 1f / 4, 1f / 2, mutableListOf(0)),
                            MindMapObject(3, "食事", 1f / 2, 3f / 4, mutableListOf(0)),
                            MindMapObject(4, "宿泊", 3f / 4, 1f / 2, mutableListOf(0))
                    )),
                    Event("学会", mutableListOf()),
                    Event("USA", mutableListOf())
            ))

    var layoutWidth = 0f
    var layoutHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null)
            toOwnPageFragment()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        layoutWidth = container.width.toFloat()
        layoutHeight = container.height.toFloat()
//        Toast.makeText(this, "${container?.width}, ${container?.height}", Toast.LENGTH_SHORT).show()
    }

    private fun toOwnPageFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, OwnPageFragment.newInstance(user))
//             初期状態のため戻るボタンで戻らない   .addToBackStack(null)
                .commit()
    }


}
