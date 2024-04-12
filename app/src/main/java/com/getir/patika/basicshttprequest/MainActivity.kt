package com.getir.patika.basicshttprequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.getir.patika.basicshttprequest.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    /*
    private lateinit var textGet: TextView
    private lateinit var textPost: TextView
    private lateinit var buttonGet: Button
    private lateinit var buttonPost: Button
    */
    // Binding
    private lateinit var binding: ActivityMainBinding
    // Base URL
    private val BASE_URL = "https://espresso-food-delivery-backend-cc3e106e2d34.herokuapp.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /*
        textGet = findViewById(R.id.txt_get)
        textPost = findViewById(R.id.txt_post)
        buttonGet = findViewById(R.id.btn_get)
        buttonPost = findViewById(R.id.btn_post)
        */

        binding.btnGet.setOnClickListener { fetchProfile() }
        binding.btnPost.setOnClickListener { performLogin() }

    }

    private fun fetchProfile() {
        val url = "${BASE_URL}profile/368b983c-d144-41b5-b0d5-8c3a8884940a"
        lifecycleScope.launch {
            val response = getProfile(url)
            binding.txtGet.text = response
        }
    }

    private fun performLogin() {
        val url = "${BASE_URL}login"
        val email = "m.ozandastan@gmail.com"
        val passwordTrue = "Qw123123"
        val passwordFalse = "Qw123123a"
        lifecycleScope.launch {
            val response = postLogin(url, email, passwordTrue)
            binding.txtPost.text = response
        }
    }

    private suspend fun getProfile(url: String): String {
        //Async
        return withContext(Dispatchers.IO) {
            // Connection to the given URL
            val connection = URL(url).openConnection() as HttpURLConnection
            // Specify that a GET request
            connection.requestMethod = "GET"
            val response = StringBuilder()
            try {
                // Read the InputStream from the connection and concatenate the response using a StringBuilder
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    var inputLine: String?
                    while (reader.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                }
            } finally {
                // Close the connection
                connection.disconnect()
            }
            // Return the response as a String
            response.toString()
        }
    }

    // POST
    private suspend fun postLogin(url: String, email: String, password: String): String {
        //Async
        return withContext(Dispatchers.IO) {
            // Create JSON data with email and password values which are needed to post Login method
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("password", password)
            }
            // Connection to the given URL
            val connection = URL(url).openConnection() as HttpURLConnection
            // Specify that a POST
            connection.requestMethod = "POST"
            // Output will be written
            connection.doOutput = true
            // Specify that the request body is in JSON format
            connection.setRequestProperty("Content-Type", "application/json")


            // Create a writer to write data
            val writer = OutputStreamWriter(connection.outputStream)
            // Write JSON data to the connection
            writer.write(jsonObject.toString())
            //// Flush the writer to ensure all data is written to the output stream immediately
            writer.flush()
            // Close the writer
            writer.close()

            // Get the response code
            val responseCode = connection.responseCode
            val response = StringBuilder()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // If successful, read the InputStream and concatenate the response using a StringBuilder
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                }
            } else {
                // If there is an error, read the errorStream and concatenate the response using a StringBuilder
                BufferedReader(InputStreamReader(connection.errorStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                }
            }
            // Close the connection
            connection.disconnect()
            // Return the response as a String
            response.toString()
        }
    }
}