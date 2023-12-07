package com.codecrafters.meditationapp.views
// Import necessary libraries
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codecrafters.meditationapp.LoginActivity
import com.codecrafters.meditationapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Quote(
    val _id: String,
    val tags: List<String>,
    val content: String,
    val author: String,
    val authorSlug: String,
    val length: Int,
    val dateAdded: String
)

interface QuoteApiService {
    @GET("/random")
    fun getRandomQuote(): Call<Quote>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.quotable.io"

    val apiService: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
    }
}

class QuoteActivity : AppCompatActivity() {

    // Declare quote at the class level
    private var quote: Quote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        // Fetch a random quote
        val call = RetrofitClient.apiService.getRandomQuote()
        call.enqueue(object : Callback<Quote> {
            override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                if (response.isSuccessful) {
                    // Assign the quote to the class-level variable
                    quote = response.body()
                    // Now you can use the quote object as needed
                    // For example, update the UI with the quote content
                    updateUIWithQuote(quote?.content)

                    // Delay for 10 seconds before moving to the next activity
                    Handler(Looper.getMainLooper()).postDelayed({
                        startNextActivity()
                    }, 5000)
                }
            }

            override fun onFailure(call: Call<Quote>, t: Throwable) {
                // Handle failure, e.g., display an error message
            }
        })

        // Set up the share button
        val shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            // Use the class-level quote variable for sharing
            shareQuote(quote?.content)
        }
    }

    private fun startNextActivity() {
        // Start the next activity (e.g., MainActivity) after the delay
        val intent = Intent(this@QuoteActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()  // finish the current activity
    }

    private fun updateUIWithQuote(quoteContent: String?) {
        // Update your UI with the quote content
        // Example: set the text of a TextView
        val quoteTextView = findViewById<TextView>(R.id.quoteTextView)
        quoteTextView.text = quoteContent
    }

    private fun shareQuote(quoteContent: String?) {
        // Implement the logic to share the quote content
        // For example, create an Intent to share text
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, quoteContent)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Quote"))
    }
}