package com.devotee.webcrawler

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class WebCrawlerViewModel : ViewModel() {
    var urlList = MutableLiveData<ArrayList<WebCrawlerModel>>()
    var newUrlList = arrayListOf<WebCrawlerModel>()
    private lateinit var urlQueue: Queue<String>
    private lateinit var visitedURLs: List<String>
    lateinit var filteredUrl: List<String>


    fun crawl(rootURL: String?) {

        urlQueue = LinkedList()
        filteredUrl = LinkedList()
        visitedURLs = java.util.ArrayList()

        var breakpointLocal = 100
        urlQueue.add(rootURL)
        rootURL?.let { (filteredUrl as LinkedList<String>).add(it) }
        rootURL?.let { (visitedURLs as java.util.ArrayList<String>).add(it) }
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

    fun getBreakpoint(breakpoint: Int, matcher: Matcher): Int {
        var breakpointLocal = breakpoint
        while (matcher.find()) {
            val actualURL = matcher.group()
            if (!visitedURLs.contains(actualURL)) {
                actualURL.let { (visitedURLs as java.util.ArrayList<String>).add(it) }
                urlQueue.add(actualURL)
                actualURL.let { (filteredUrl as LinkedList<String>).add(it) }

                //   binding.urlList.layoutManager = LinearLayoutManager(applicationContext)
                //  binding.urlList.adapter = listAdapter


                var urlListObj = WebCrawlerModel(actualURL)

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