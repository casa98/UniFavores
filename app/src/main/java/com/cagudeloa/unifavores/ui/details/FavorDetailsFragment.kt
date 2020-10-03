package com.cagudeloa.unifavores.ui.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor

class FavorDetailsFragment : Fragment() {

    private lateinit var favor: Favor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            favor = it.getParcelable("favor")!!
        }
        Log.i("ITEM CLICK", favor.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favor_details, container, false)
    }
}