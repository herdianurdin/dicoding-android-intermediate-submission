package com.saeware.storyapp.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saeware.storyapp.R
import com.saeware.storyapp.adapter.LoadingAdapter
import com.saeware.storyapp.adapter.StoryAdapter
import com.saeware.storyapp.data.local.entity.Story
import com.saeware.storyapp.databinding.FragmentHomeBinding
import com.saeware.storyapp.ui.detail.DetailActivity
import com.saeware.storyapp.ui.detail.DetailActivity.Companion.EXTRA_DETAIL
import com.saeware.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN
import com.saeware.storyapp.ui.post.PostActivity
import com.saeware.storyapp.utils.MessageUtility.showToast
import com.saeware.storyapp.utils.ViewExtensions.setVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPagingApi
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel: HomeViewModel by viewModels()

    private var token: String = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter

    private val launchPostActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { getStories() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        token = requireActivity().intent.getStringExtra(EXTRA_TOKEN)!!

        setRecyclerView()
        getStories()
        swipeRefresh()

        binding?.fabPost?.setOnClickListener {
            val postIntent = Intent(requireContext(), PostActivity::class.java)
            postIntent.putExtra(EXTRA_TOKEN, token)

            launchPostActivity.launch(postIntent)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(R.string.exit)
                        setMessage(R.string.confirmation_message)
                        setPositiveButton(R.string.yes) { _, _ -> requireActivity().finish()}
                        setNegativeButton(R.string.no) { _, _ -> }
                        create()
                        show()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        getStories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getStories() {
        viewModel.getAllStories(token).observe(viewLifecycleOwner) {
            updateAdapterState(it)
        }
    }

    private fun updateAdapterState(stories: PagingData<Story>) {
        storyAdapter.submitData(lifecycle, stories)
        recyclerView.smoothScrollToPosition(0)
    }

    private fun setRecyclerView() {
        storyAdapter = StoryAdapter()
        storyAdapter.addLoadStateListener {
            if ((it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && storyAdapter.itemCount < 1) || it.source.refresh is LoadState.Error) showErrorOccurred(true)
            else showErrorOccurred(false)

            binding?.swipeRefresh?.isRefreshing = it.source.refresh is LoadState.Loading
        }
        storyAdapter.setOnStartActivityCallback(object : StoryAdapter.OnStartActivityCallback {
            override fun onStartActivityCallback(story: Story, bundle: Bundle?) {
                Intent(requireContext(), DetailActivity::class.java).also {
                    it.putExtra(EXTRA_DETAIL, story)
                    startActivity(it, bundle)
                }
            }
        })

        try {
            recyclerView = binding?.rvStories!!
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = storyAdapter.withLoadStateFooter(
                    footer = LoadingAdapter {
                        storyAdapter.retry()
                    }
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun swipeRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener { getStories() }
    }

    private fun showErrorOccurred(isError: Boolean) {
        binding?.apply {
            tvErrorResult.setVisible(isError)
            rvStories.setVisible(!isError)
        }

        if (isError) showToast(requireContext(), getString(R.string.error_occurred))
    }
}