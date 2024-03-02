package com.codecrafters.meditationapp

// Importing necessary Android libraries
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

// Defining the ViewPagerAdapter class
class ViewPagerAdapter (fm:FragmentManager) : FragmentStatePagerAdapter(fm){
    // Initializing lists to hold fragments and their titles
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    // Overriding getItem method to return the fragment at the specified position
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    // Overriding getCount method to return the total number of fragments
    override fun getCount(): Int {
        return mFragmentList.size
    }

    // Overriding getPageTitle method to return the title of the fragment at the specified position
    override fun getPageTitle(position: Int): CharSequence? = mFragmentTitleList[position]

    // Method to add a fragment along with its title to the adapter
    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
}
