package com.devotee.webcrawler

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devotee.webcrawler.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var urlQueue: Queue<String>
    private lateinit var visitedURLs: List<String>
    private lateinit var filteredUrl: List<String>


    private lateinit var listAdapter: ListAdapter
    private lateinit var viewModel: WebCrawlerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        viewModel =  ViewModelProvider(this, WebCrawlerViewModelFactory()).get(WebCrawlerViewModel::class.java)
        binding.urlList.layoutManager = LinearLayoutManager(applicationContext)


        urlQueue = LinkedList()
        filteredUrl = LinkedList()
        visitedURLs = ArrayList()

        binding.clickMe.setOnClickListener {
            (filteredUrl as LinkedList<String>).clear()

            val rootURL: String = binding.urlEv.text.toString()
            crawl(rootURL)

        }

    }


    private fun crawl(rootURL: String?) {
        var breakpointLocal = 100
        urlQueue.add(rootURL)
        rootURL?.let { (filteredUrl as LinkedList<String>).add(it) }
        rootURL?.let { (visitedURLs as ArrayList<String>).add(it) }
        while (!urlQueue.isEmpty()) {

            // remove the next url string from the queue to begin traverse.
            val s = urlQueue.remove()
            var rawHTML: String? = ""
            try {
                // create url with the string.
                val url = URL(s)
                val `in` = BufferedReader(InputStreamReader(url.openStream()))
                var inputLine = `in`.readLine()
                println("Data InputLine :: $inputLine")

                // read every line of the HTML content in the URL
                // and concat each line to the rawHTML string until every line is read.
                while (inputLine != null) {
                    rawHTML += inputLine
                    inputLine = `in`.readLine()
                }
                `in`.close()


            } catch (e: Exception) {
                e.printStackTrace()
            }

            val urlPattern = "(www|http:|https)+[^s]+[\\w]"
            val pattern = Pattern.compile(urlPattern)
            val matcher = pattern.matcher(rawHTML.toString())

            // Each time the regex matches a URL in the HTML,
            // add it to the queue for the next traverse and to the list of visited URLs.
            breakpointLocal = getBreakpoint(breakpointLocal, matcher)

            // exit the outermost loop if it reaches the breakpoint.
            if (breakpointLocal == 0) {
                break
            }
        }
    }

    private fun getBreakpoint(breakpoint: Int, matcher: Matcher): Int {
        var breakpointLocal = breakpoint
        while (matcher.find()) {
            val actualURL = matcher.group()
            if (!visitedURLs.contains(actualURL)) {
                actualURL.let { (visitedURLs as ArrayList<String>).add(it) }
                urlQueue.add(actualURL)
                actualURL.let { (filteredUrl as LinkedList<String>).add(it) }
                listAdapter = ListAdapter( filteredUrl)
                binding.urlList.layoutManager = LinearLayoutManager(applicationContext)
                binding.urlList.adapter = listAdapter
            }

            // exit the loop if it reaches the breakpoint.
            if (breakpointLocal == 0) {
                break
            }
            breakpointLocal--
        }
        return breakpointLocal
    }
}