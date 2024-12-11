package com.kenkoro.practice.githubClone.reposViewer.presentation.reposList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkoro.practice.githubClone.reposViewer.presentation.models.RepoUi
import com.kenkoro.projects.githubClone.R
import com.kenkoro.projects.githubClone.databinding.ReposListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReposListFragment : Fragment() {
  private var _binding: ReposListFragmentBinding? = null
  private val binding: ReposListFragmentBinding
    get() = _binding!!

  private lateinit var rvRepos: RecyclerView
  private lateinit var pbLoadingBar: ProgressBar
  private lateinit var rlWarningContainer: RelativeLayout
  private lateinit var ivWarningIcon: ImageView
  private lateinit var tvWarningTitle: TextView
  private lateinit var tvWarningDescription: TextView
  private lateinit var btnRetry: AppCompatButton
  private val reposListViewModel by viewModels<ReposListViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    _binding = ReposListFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    setupBindings()
    subscribeToObservables()

    btnRetry.setOnClickListener { reposListViewModel.onRetryButtonPressed() }
  }

  private fun setupBindings() {
    rvRepos = binding.rvRepos
    pbLoadingBar = binding.pbLoadingBar
    rlWarningContainer = binding.rlWarningContainer
    ivWarningIcon = binding.ivWarningIcon
    tvWarningTitle = binding.tvWarningTitle
    tvWarningDescription = binding.tvWarningDescription
    btnRetry = binding.btnRetry
  }

  private fun subscribeToObservables() {
    reposListViewModel.state.observe(viewLifecycleOwner) { state ->
      when (state) {
        ReposListViewModel.State.Empty -> onEmptyState()
        is ReposListViewModel.State.Error -> onErrorState(state.error)
        is ReposListViewModel.State.Loaded -> onLoadedState(state.repos)
        ReposListViewModel.State.Loading -> onLoadingState()
      }
    }
  }

  private fun onLoadedState(repos: List<RepoUi>) {
    showReposList()

    val rvAdapter =
      ReposAdapter(repos).apply {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
      }
    rvRepos.adapter = rvAdapter
    rvRepos.layoutManager = LinearLayoutManager(requireContext())
  }

  private fun onLoadingState() {
    showLoadingBar()
  }

  private fun onErrorState(error: String) {
    showWarningContainer()

    val keyword = requireContext().getString(R.string.no_internet_error_keyword)
    if (error.contains(keyword, true)) {
      ivWarningIcon.setImageResource(R.drawable.connection_error_icon)
      tvWarningTitle.setText(R.string.warning_container_no_internet_error_title)
    } else {
      ivWarningIcon.setImageResource(R.drawable.error_icon)
      tvWarningTitle.setText(R.string.warning_container_other_error_title)
    }
    tvWarningDescription.text = error
    tvWarningTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.soft_error))
    btnRetry.setText(R.string.btn_retry_text_on_error)
  }

  private fun onEmptyState() {
    showWarningContainer()

    ivWarningIcon.setImageResource(R.drawable.empty_repos_list_icon)
    tvWarningTitle.setText(R.string.warning_container_empty_title)
    tvWarningDescription.setText(R.string.warning_container_empty_description)
    tvWarningTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
    btnRetry.setText(R.string.btn_retry_text_on_empty)
  }

  private fun showReposList() {
    rvRepos.visibility = View.VISIBLE
    pbLoadingBar.visibility = View.GONE
    rlWarningContainer.visibility = View.GONE
    btnRetry.visibility = View.GONE
  }

  private fun showLoadingBar() {
    rvRepos.visibility = View.GONE
    pbLoadingBar.visibility = View.VISIBLE
    rlWarningContainer.visibility = View.GONE
    btnRetry.visibility = View.GONE
  }

  private fun showWarningContainer() {
    rvRepos.visibility = View.GONE
    pbLoadingBar.visibility = View.GONE
    rlWarningContainer.visibility = View.VISIBLE
    btnRetry.visibility = View.VISIBLE
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}