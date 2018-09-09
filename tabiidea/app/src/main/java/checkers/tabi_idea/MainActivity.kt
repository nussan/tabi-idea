package checkers.tabi_idea

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    val user: UserParceler = UserParceler(0, "たきかわ", mutableListOf(Event("研究室旅行")))

    val user: User = User(
            0,
            "たきかわ",
            mutableListOf(
                    Event("研究室旅行"),
                    Event("学会"),
                    Event("USA")
                    ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null)
            toOwnPageFragment()
    }

    private fun toOwnPageFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, OwnPageFragment.newInstance(user))
//             初期状態のため戻るボタンで戻らない   .addToBackStack(null)
                .commit()
    }


}
