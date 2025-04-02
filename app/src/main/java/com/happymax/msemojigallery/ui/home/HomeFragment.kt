package com.happymax.msemojigallery.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happymax.msemojigallery.Emoji
import com.happymax.msemojigallery.EmojiCategory
import com.happymax.msemojigallery.EmojiListAdapter
import com.happymax.msemojigallery.Helper
import com.happymax.msemojigallery.MainActivity
import com.happymax.msemojigallery.R
import com.happymax.msemojigallery.databinding.FragmentHomeBinding
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        /*val root: View = binding.root
        if(root != null){
            val activity = activity as? MainActivity
            if (activity != null) {
                val emojis = activity.emojis
                val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
                recyclerView.layoutManager = GridLayoutManager(context, Helper.CalculateSpanCount(recyclerView))
                recyclerView.adapter = EmojiListAdapter(emojis)
            }
        }*/
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}