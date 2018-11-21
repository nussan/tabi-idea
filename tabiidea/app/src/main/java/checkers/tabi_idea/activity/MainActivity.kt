package checkers.tabi_idea.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.Installation
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.EventListFragment
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
            Log.d("editprob", uuid)
            repository.getUser(uuid) {
                if (it.id == -1) {
                    Log.d("editprob","X")
                    val newUser = mapOf(
                            "uuid" to uuid,
                            "name" to "TAKIKAWA"
                    )
                    repository.addUser(newUser) {user: User ->
                        user.token = "Authorization: Token " + user.token
                        repository.getEventList(user!!.id) {
                            toEventListFragment(user, it)
                        }
                    }
                } else {
                    Log.d("editprob","O")
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

    //追加しました
    private fun toEventListFragment(user: User, eventList: MutableList<Event>) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, EventListFragment.newInstance(user,eventList))
                .commit()
    }
}
