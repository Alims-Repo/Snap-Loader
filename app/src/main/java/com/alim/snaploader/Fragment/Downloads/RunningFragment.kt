package com.alim.snaploader.Fragment.Downloads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.alim.snaploader.Interface.ProgressInterface
import com.alim.snaploader.R
import com.alim.snaploader.Reciever.ProgressReceiver

class RunningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_running, container, false)

        val progressBar = rootView.findViewById<ProgressBar>(R.id.running_prog)

        ProgressReceiver().register(object: ProgressInterface {
            override fun Progress(pro: Int) {
                progressBar.progress = pro
            }
        })

        return rootView
    }

}
