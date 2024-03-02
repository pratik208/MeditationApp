package com.codecrafters.meditationapp

import OnboardingItem
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {
    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Initialize SharedPreferences
        preferences = applicationContext.getSharedPreferences("ONBOARD", Context.MODE_PRIVATE)

        // Set up onboarding items
        setOnboardingItems()

        // Set up indicators
        setupIndicators()

        // Set current indicator to the first item
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems() {
        // Create adapter with onboarding items
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.img_1,
                    title = "Welcome to Boostify App",
                    description = "Empower Your Happiness with Boostify And Elevate Your Mood with Boostify."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.img_2,
                    title = "Community",
                    description = "Connect, Chat, and Find Support in Our Empowering Community"
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.img_3,
                    title = "Expert Advice at Your Fingertips",
                    description = "Consult with Trusted Doctors on the Consulting Board."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.img_4,
                    title = "MeditateMe",
                    description = "Explore Serenity with 'MeditateMe' on Boostify."
                ),
                /*   OnboardingItem(
                       onboardingImage = R.drawable.img_paint,
                       title = "Courses for absolute beginners ",
                       description = "In fact, we made a quick video to help you learn the basics"
                   ),*/
                OnboardingItem(
                    onboardingImage = R.drawable.ready,
                    title = "Ready!",
                    description = "Now, get ready to sit back and enjoy Boostify application"
                )
            )
        )

        // Set up ViewPager2 with adapter
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter

        // Handle page change to update indicators
        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        // Disable overscroll effect
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        // Handle "Next" button click
        findViewById<ImageView>(R.id.imageNext).setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                onboardingViewPager.currentItem += 1
            } else {
                // Navigate to home activity when onboarding is completed
                navigateToHomeActivity()
                setCompleteOnboarding()
            }
        }

        // Handle "Skip" text click
        findViewById<TextView>(R.id.textSkip).setOnClickListener {
            navigateToHomeActivity()
        }

        // Handle "Get Started" button click
        findViewById<MaterialButton>(R.id.buttonGetStarted).setOnClickListener {
            navigateToHomeActivity()
            setCompleteOnboarding()
        }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun setCompleteOnboarding() {
        // Set onboarding completion status in SharedPreferences
        preferences.edit().putBoolean("ISCOMPLETE", true).apply()
    }

    private fun setupIndicators() {
        // Find indicator container
        indicatorContainer = findViewById(R.id.indicatorsContainer)

        // Create indicators and add them to the container
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        // Set the indicator corresponding to the current page
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            imageView.scaleType = ImageView.ScaleType.FIT_END
            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            imageView.layoutParams = layoutParams
            if (i == position) {
                // Set active indicator
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
            } else {
                // Set inactive indicator
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_backgound
                    )
                )
            }
        }
    }
}
