package com.alim.snaploader.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alim.snaploader.Adapter.RecyclerAdapter
import com.alim.snaploader.Config.ConstantConfig
import com.alim.snaploader.Model.YoutubeData

import com.alim.snaploader.R
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONObject

class MusicFragment : Fragment() {

    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapter
    val data: ArrayList<YoutubeData> = ArrayList()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

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
        val httpClient: HttpClient = DefaultHttpClient()
        val httpGet = HttpGet(ConstantConfig(activity!!).getContent("music"))
        val response: HttpResponse = httpClient.execute(httpGet)
        val httpEntity: HttpEntity = response.entity
        //Game Starts Here
        val json = JSONObject(EntityUtils.toString(httpEntity))
        try {
            val nextToken = json.getString("nextPageToken")
        } catch (e: Exception) {}
        val dataPerPage = json.getJSONObject("pageInfo").getInt("resultsPerPage")-1
        val jsonA = json.getJSONArray("items")

        //Log.println(Log.ASSERT,"Items", jsonA.toString())

        for (x in 0..dataPerPage) {
            try {
                val youtubeData = YoutubeData()
                val obj = jsonA.getJSONObject(x)
                val snip = jsonA.getJSONObject(x).getJSONObject("snippet")

                youtubeData.title = snip.getString("title")
                youtubeData.date = snip.getString("publishedAt")
                youtubeData.channelI = snip.getString("channelId")
                youtubeData.channelN = snip.getString("channelTitle")
                youtubeData.description = snip.getString("description")
                youtubeData.id = obj.getString("id")
                youtubeData.thumbnail = snip.getJSONObject("thumbnails").getJSONObject("high").getString("url")

                data.add(youtubeData)
            } catch (e: java.lang.Exception) {
                Log.println(Log.ASSERT,"Exception", e.toString())
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
