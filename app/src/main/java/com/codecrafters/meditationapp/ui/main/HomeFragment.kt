package com.codecrafters.meditationapp.ui.main

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseUser
import com.codecrafters.meditationapp.BreathActivity
import com.codecrafters.meditationapp.MeditationActivity
import com.codecrafters.meditationapp.R
import com.codecrafters.meditationapp.helper.NotificationReceiver
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.report.view.*
import java.util.*

class HomeFragment : Fragment() {
    lateinit var currentUser: FirebaseUser
    private lateinit var alarmManager: AlarmManager
    private lateinit var calendar: Calendar
    private lateinit var picker: MaterialTimePicker
    private lateinit var database : DatabaseReference
    private lateinit var viewModel: HomeViewModel
    lateinit var pView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        pView = view
        calendar = Calendar.getInstance()
        createNotificationChannel()

        currentUser = FirebaseAuth.getInstance().currentUser!!
        val uid = FirebaseAuth.getInstance().uid

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.init(requireContext(), uid!!)

        updateReportTexts(view)

        // Retrieve user data from Firebase Database
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                textView3.text = it.child("firstname").value.toString()
            } else {
                Toast.makeText(context, "Check your Internet connection.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Couldn't find the user. Internet connection have to connected properly.", Toast.LENGTH_SHORT).show()
        }

        // Set up button click listeners for meditation and breathing activities
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        view.button.setOnClickListener {
            startActivity(Intent(context, MeditationActivity::class.java))
        }

        view.breathe.setOnClickListener {
            startActivity(Intent(context, BreathActivity::class.java))
        }

        // Set up reminder button click listener
        view.remind.setOnClickListener {
            picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Reminder time")
                .build()

            activity?.let { it1 -> picker.show(it1.supportFragmentManager, "GENO") }

            picker.addOnPositiveButtonClickListener {
                cancelAlarm()
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendar.set(Calendar.MINUTE, picker.minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Set up alarm for reminder notification
                val intent = Intent(context, NotificationReceiver::class.java)
                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this.context, 200, intent, 0)
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                Toast.makeText(context, "Successfully set reminder at: ${calendar.get(Calendar.HOUR_OF_DAY).toString()}:${calendar.get(Calendar.MINUTE).toString()} every day.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Update text fields in the report section
    private fun updateReportTexts(view: View) {
        view.val_meditate_times.text = viewModel.getMeditationCount().toString()
        view.val_meditate.text = viewModel.getMeditationMin().toString()
        view.val_breathe_times.text = viewModel.getBreatheCount().toString()
        view.val_breathe.text = viewModel.getBreatheMin().toString()
    }

    // Update progress bar and text fields in the report section
    private fun updateReportProgress(view: View) {
        val medMin = viewModel.getMeditationMin()
        val breMin = viewModel.getBreatheMin()
        val medCount = viewModel.getMeditationCount()
        val breCount = viewModel.getBreatheCount()
        var percentage = 1
        if(medCount > 0 && breCount > 0) {
            percentage = (medMin + breMin) * 100 / (medCount * 20) + (breCount * 3)
        }
        view.statusBar.progress = percentage
        view.val_meditate_times.text = medCount.toString()
        view.val_meditate.text = medMin.toString()
        view.val_breathe_times.text = breCount.toString()
        view.val_breathe.text = breMin.toString()
    }

    override fun onResume() {
        updateReportTexts(pView)
        updateReportProgress(pView)
        super.onResume()
    }

    // Create notification channel for reminders (Android O and above)
    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var name: CharSequence = "MORNING Meditation Channel"
            var description: String = "Channel for morning meditation reminder"
            var importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            var channel: NotificationChannel = NotificationChannel("MORNING", name, importance)
            channel.description = description
            var notificationManager: NotificationManager? = context?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    // Cancel the reminder alarm
    fun cancelAlarm() {
        var intent = Intent(context, MeditationActivity::class.java)
        var pendingInteng = PendingIntent.getBroadcast(context, 200, intent, 0)

        if(alarmManager == null) {
            alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }

        alarmManager.cancel(pendingInteng)
    }
}
