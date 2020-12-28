package com.example.activitylifetime.lesson3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.activitylifetime.R
import com.example.activitylifetime.lesson3.fragment.SecondFragment
import com.example.activitylifetime.lesson3.fragment.ThirdFragment
import kotlinx.android.synthetic.main.lesson3_first_fragment.*

class Lesson3FragmentActivity : AppCompatActivity() {
    private var checkFragment: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson3_fragment)
        FirFragButton.setOnClickListener {
            if (checkFragment) {
                replaceFragment(SecondFragment())
            } else {
                replaceFragment(ThirdFragment())
            }
            checkFragment = !checkFragment
        }
        replaceFragment(SecondFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.rightFrameLayout, fragment).commit()
    }
}