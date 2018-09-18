package checkers.tabi_idea

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val user: User = User(
            0,
            "たきかわ",
            mutableListOf(
                    Event("研究室旅行"),
                    Event("学会"),
                    Event("USA")
            ))

    var layoutWidth = 0
    var layoutHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null)
            toOwnPageFragment()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        layoutWidth = container.width
        layoutHeight = container.height
        Toast.makeText(this, "${container?.width}, ${container?.height}", Toast.LENGTH_SHORT).show()
    }
    private fun toOwnPageFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, OwnPageFragment.newInstance(user))
//             初期状態のため戻るボタンで戻らない   .addToBackStack(null)
                .commit()
    }


}
