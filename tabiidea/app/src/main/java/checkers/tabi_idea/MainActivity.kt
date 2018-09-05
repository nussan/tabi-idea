package checkers.tabi_idea

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toOwnPageFragment()
    }

    fun toOwnPageFragment() {
//        val fragmentManager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val ownPageFragment = OwnPageFragment.newInstance()

        fragmentTransaction.replace(R.id.container, ownPageFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
