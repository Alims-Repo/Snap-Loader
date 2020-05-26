package com.alim.snaploader.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alim.snaploader.Model.YoutubeData
import com.alim.snaploader.R
import com.alim.snaploader.Adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONObject

class HomeFragment : Fragment() {

    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapter
    val data: ArrayList<YoutubeData> = ArrayList()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        layoutManager = LinearLayoutManager(activity!!)
        progressBar = rootView.findViewById(R.id.loading)
        recyclerView = rootView.findViewById(R.id.main_recycler)
        layoutManager = LinearLayoutManager(activity!!)

        recyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter(activity!!, data)
        recyclerView.adapter = adapter

        if (data.size>0)
            progressBar.visibility = View.GONE

        Thread(getData).start()

        return rootView
    }

    private val getData = Thread {
        var URL = "http://youtube-scrape.herokuapp.com/api/search?q=&page=1"
        //var URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&regionCode=US&order=relevance&maxResults=50&key=AIzaSyAziPdq7YyRtGCFUefxD6wuST_s48Ihp-k"
        val httpClient: HttpClient = DefaultHttpClient()
        val httpGet = HttpGet(URL)
        val response: HttpResponse = httpClient.execute(httpGet)
        Log.println(Log.ASSERT,"RESPONSE", "response.toString()")
        val httpEntity: HttpEntity = response.entity
        val json = JSONObject(EntityUtils.toString(httpEntity)).getJSONArray("results")

        //val tag = "Home Frag"
        for (x in 0..20) {
            try {
                val youtubeData = YoutubeData()

                val son = json.getJSONObject(x).getJSONObject("video")

                //Add data to Data Model
                youtubeData.id = son.getString("id").toString()
                youtubeData.title = son.getString("title").toString()
                youtubeData.length = son.getString("duration").toString()
                youtubeData.views = son.getString("upload_date").toString()
                youtubeData.thumbnail = son.getString("thumbnail_src").toString()

                /**Log.println(Log.ASSERT, tag, son.getString("id").toString())
                Log.println(Log.ASSERT, tag, son.getString("url").toString())
                Log.println(Log.ASSERT, tag, son.getString("title").toString())
                Log.println(Log.ASSERT, tag, son.getString("duration").toString())
                Log.println(Log.ASSERT, tag, son.getString("upload_date").toString())
                Log.println(Log.ASSERT, tag, son.getString("thumbnail_src").toString())*/

                data.add(youtubeData)

            } catch (e: Exception) {
                Log.println(Log.ASSERT, "Home Frag Ex", "$e")
            }
        }
        try {
            activity!!.runOnUiThread {
                progressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.println(Log.ASSERT, "Home Frag Ex", "$e")
        }
    }
}