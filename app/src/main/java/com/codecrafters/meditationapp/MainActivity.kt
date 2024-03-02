package com.codecrafters.meditationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codecrafters.meditationapp.ui.main.AboutFragment
import com.codecrafters.meditationapp.ui.main.HomeFragment
import com.codecrafters.meditationapp.ui.main.ForumFragment
import com.codecrafters.meditationapp.ui.main.LearnFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the ViewPagerAdapter
        val adapter = ViewPagerAdapter(supportFragmentManager)

        // Add fragments to the adapter with corresponding titles
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(LearnFragment(), "Learn")
        adapter.addFragment(ForumFragment(), "Forum")
        adapter.addFragment(AboutFragment(), "About")

        // Set the adapter to the ViewPager
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        tabs.setupWithViewPager(viewPager)

        // Set icons for each tab
        tabs.getTabAt(0)!!.setIcon(R.drawable.home)
        tabs.getTabAt(1)!!.setIcon(R.drawable.learn)
        tabs.getTabAt(2)!!.setIcon(R.drawable.forum)
        tabs.getTabAt(3)!!.setIcon(R.drawable.about)
    }
}
