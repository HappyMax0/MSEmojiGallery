package com.happymax.msemojigallery

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FavouritesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MainActivity
        if (activity != null) {
            val mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)

            val emojis = mainViewModel.emojis.value.filter { it.collected }
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    layoutManager.spanCount = Helper.CalculateSpanCount(view)
                }
            })

            recyclerView.layoutManager = GridLayoutManager(context, Helper.CalculateSpanCount(view))
            val adapter = emojis?.let { EmojiListAdapter(it) }
            adapter?.setOnClickListener(object :
                EmojiListAdapter.OnClickListener {
                override fun onClick(position: Int, model: Emoji) {
                    val detailFragment = activity.findViewById<View>(R.id.detail_fragment)  //supportFragmentManager.findFragmentByTag("detailFragment")
                    if(detailFragment != null){
                        // 在平板模式下，更新 CategoryFragment 显示
                        val manager = activity.supportFragmentManager
                        //val detailFragment = manager.findFragmentById(R.id.detailFragment) as? DetailFragment
                        val detailFragment = DetailFragment().apply {
                            arguments = Bundle().apply {
                                putString("name", model.name)  // 传递参数
                                putBoolean("hasMultiSkin", model.hasMultiSkin)
                                putBoolean("collected", model.collected)
                            }
                        }
                        manager.beginTransaction()
                            .replace(R.id.detail_fragment, detailFragment)
                            .commit()
                    }
                    else{
                        //findNavController().navigate(R.id.detail_fragment, bundleOf("name" to model.name))
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("name", model.name)
                        intent.putExtra("hasMultiSkin", model.hasMultiSkin)
                        intent.putExtra("collected", model.collected)
                        startActivity(intent)
                    }
                }
            })
            recyclerView.adapter = adapter
        }

    }
}