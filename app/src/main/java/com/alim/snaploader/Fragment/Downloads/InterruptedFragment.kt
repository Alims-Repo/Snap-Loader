package com.alim.snaploader.Fragment.Downloads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alim.snaploader.R

/**
 * A simple [Fragment] subclass.
 */
class InterruptedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interrupted, container, false)
    }

}
