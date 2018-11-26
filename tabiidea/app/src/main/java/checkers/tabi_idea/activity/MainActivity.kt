package checkers.tabi_idea.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.Installation
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.EventListFragment
import checkers.tabi_idea.provider.Repository
import checkers.tabi_idea.provider.RequestService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_event_list.*


class MainActivity : AppCompatActivity() {
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
                Log.d("usertoken",it.token)
                if (it.id == -1) {
                    Log.d("editprob", "X")
                    val newUser = mapOf(
                            "uuid" to uuid,
                            "name" to "新しいユーザー"
                    )
                    repository.addUserMock(newUser) {user: User -> //TODO 要変更
                        repository.getEventList(user.token,user!!.id) {
                            toEventListFragment(user, it)
                        }
                    }
                } else {
                    Log.d("editprob", "O")
                    Log.d("usertoken",it.token)
                    repository.getEventList(it.token,it.id) { evel: MutableList<Event> ->
                        toEventListFragment(it, evel)
                    }
                }
            }
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    //追加しました
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
}
