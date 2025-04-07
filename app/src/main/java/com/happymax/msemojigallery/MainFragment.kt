package com.happymax.msemojigallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happymax.msemojigallery.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    var category:EmojiCategory? = EmojiCategory.ALL
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        category = arguments?.getString("category")
            ?.let { EmojiCategory.valueOf(it) }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MainActivity
        if (activity is MainActivity) {
            val mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)

            val emojis = if (category == EmojiCategory.FAVOURITE) mainViewModel.emojis.value.filter { it.collected }
            else mainViewModel.emojis.value.filter { it.category == category }

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
                        activity.launcher?.launch(intent)
                    }
                }
            })
            recyclerView.adapter = adapter

            parentFragmentManager.setFragmentResultListener(
                "favouriteChanged", this
            ) { key: String?, bundle: Bundle ->
                val name = bundle.getString("name")
                val favourited = bundle.getBoolean("collected")
                val emoji = emojis?.first{ it.name == name }
                if(emoji != null)
                    emoji.collected = favourited

                Toast.makeText(context, "favouriteChanged: $name, Fav: $favourited", Toast.LENGTH_SHORT).show()
                Log.d("MainFragment", "favouriteChanged: $name, Fav: $favourited");

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}