package checkers.tabi_idea.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Installation
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.EventListFragment
import checkers.tabi_idea.fragment.OwnPageFragment
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //sIdを取得
        val uuid = Installation.id(this)

        if( savedInstanceState == null) {
            Log.d("uuid", uuid)
            repository.getUser("tsubasa") {
                if (it.id == -1) {
                    val newUser = mapOf(
                            "uuid" to uuid,
                            "name" to "新しいユーザー"
                    )
                    repository.addUser(newUser) {user: User ->
                        repository.getEventList(user!!.id) {
                            toEventListFragment(user, it)
                        }
                    }
                } else {
                    repository.getEventList(it.id) { evel : MutableList<Event> ->
                        toEventListFragment(it, evel)
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    private fun toOwnPageFragment(user: User) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, OwnPageFragment.newInstance(user))
//             初期状態のため戻るボタンで戻らない   .addToBackStack(null)
                .commit()
    }
    //追加しました
    private fun toEventListFragment(user: User, eventList: MutableList<Event>) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, EventListFragment.newInstance(user,eventList))
                .commit()
    }
}
