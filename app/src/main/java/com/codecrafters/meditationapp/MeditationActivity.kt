package com.codecrafters.meditationapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.codecrafters.meditationapp.databinding.ActivityMeditationBinding
import com.codecrafters.meditationapp.ui.main.HomeViewModel
import kotlinx.android.synthetic.main.activity_meditation.*
import java.util.*
import java.util.concurrent.TimeUnit

class MeditationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeditationBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var isFullscreen: Boolean = false
    private lateinit var tts: TextToSpeech
    private lateinit var values: Array<String>
    private var minutes = 20L
    private var startMin = 20L
    private lateinit var textIndicator: TextView
    private lateinit var timer: CountDownTimer
    private var isVoiceEnabled: Boolean = true
    private lateinit var viewModel: HomeViewModel
    lateinit var currentUser: FirebaseUser
    private var isRunning: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating the layout using ViewBinding
        binding = ActivityMeditationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enabling the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isFullscreen = true

        // Getting the current user
        currentUser = FirebaseAuth.getInstance().currentUser!!
        val uid = FirebaseAuth.getInstance().uid

        // Initializing the ViewModel
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.init(applicationContext, uid!!)

        // Initializing UI components
        textIndicator = indicator

        // Setting up the spinner for selecting meditation duration
        values = resources.getStringArray(R.array.minutes)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, values)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                minutes = if (position == 0) 20L else 10L
                startMin = minutes
                textIndicator.text = "$minutes:00"
                timer.cancel()
                timer = createTimer()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Initializing MediaPlayer with the background sound
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.back_sound)
        timer = createTimer()

        // Setting up click listeners for buttons
        close.setOnClickListener {
            showDialog(this)
        }

        speech.setOnClickListener {
            if (isVoiceEnabled) {
                isVoiceEnabled = false
                speech.setImageResource(R.drawable.mic_no)
            } else {
                isVoiceEnabled = true
                speech.setImageResource(R.drawable.mic)
            }
        }

        start.setOnClickListener {
            timer.start()
            spinner.isEnabled = false
            spinner.isClickable = false
            mediaPlayer.isLooping = true
            mediaPlayer.start()
            start.isClickable = false
            start.text = "Started"
            isRunning = true
        }

        sound.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                sound.setImageResource(R.drawable.sound_no)
                mediaPlayer.pause()
            } else {
                sound.setImageResource(R.drawable.sound)
                mediaPlayer.start()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Stopping the MediaPlayer and timer when the activity is stopped
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        timer.cancel()
        // Updating meditation minutes and count if the meditation was running
        if (isRunning) {
            viewModel.updateMeditationMinutes((startMin - minutes).toInt())
            viewModel.updateMeditationCount()
        }
    }

    override fun onBackPressed() {
        // Showing a confirmation dialog when the back button is pressed
        showDialog(this)
    }

    private fun createTimer(): CountDownTimer {
        return object : CountDownTimer(minutes * 60000, 1000) {
            var sec = 0L

            override fun onTick(ms: Long) {
                // Updating the timer display every second
                minutes =
                    TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms))
                sec = TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms))

                // Providing voice instructions at specific intervals during the meditation
                if (((startMin == 10L && minutes == 9L) || minutes == 19L) && sec == 55L) {
                    speak("Sit down and relaxing position and close your eyes")
                }

                if (minutes == 2L && sec == 10L) {
                    speak("Please keep your eyes closed and stop thinking mantra, take two more minutes")
                }

                if (minutes == 0L && sec == 10L) {
                    speak("No you can open your eyes.")
                }
                textIndicator.text = "${minutes.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"
            }

            override fun onFinish() {
// Providing a closing message at the end of the meditation
                speak("Thank you for doing meditation with me. I hope your feeling great right now after doing this meditation")
                viewModel.updateMeditationMinutes(20)
                viewModel.updateMeditationCount()
            }
        }
    }

    private fun speak(text: String) {
        // Setting up TextToSpeech for providing voice instructions
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if (isVoiceEnabled && it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(0.8F)
                tts.speak(text, TextToSpeech.QUEUE_ADD, null)
            }
        })
    }

    private fun showDialog(context: Context) {
        // Displaying a confirmation dialog when attempting to close the meditation
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setMessage("Do you want to stop meditating ?").setCancelable(true)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            finish()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        val alert = builder.create()
        alert.setTitle("Are you sure")
        alert.show()
    }
}
