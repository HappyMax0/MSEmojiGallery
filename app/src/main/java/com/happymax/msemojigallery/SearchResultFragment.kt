package com.happymax.msemojigallery

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

/**
 * A fragment representing a list of Items.
 */
class SearchResultFragment : Fragment() {

    private lateinit var viewModel: SearchResultViewModel
    private lateinit var itemAdapter: MyItemRecyclerViewAdapter

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_result_list, container, false)

        viewModel = ViewModelProvider( this.activity as MainActivity).get(SearchResultViewModel::class.java)

        // Set the adapter
        if (view is RecyclerView) {
            /*with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                itemAdapter = MyItemRecyclerViewAdapter(emptyList())
                adapter = itemAdapter
            }*/

            itemAdapter = MyItemRecyclerViewAdapter(emptyList())
            val activity = this.activity as MainActivity
            itemAdapter?.setOnClickListener(object :
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
                                putString("category", model.category.toString())
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
                        intent.putExtra("category", model.category.toString())
                        activity.launcher?.launch(intent)
                    }
                }
            })

            view.layoutManager = LinearLayoutManager(context)
            view.adapter = itemAdapter
        }

        // 监听数据变化
        viewModel.items.observe(viewLifecycleOwner) { updatedList ->
            itemAdapter.updateData(updatedList)
        }

        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}