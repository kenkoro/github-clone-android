package com.kenkoro.practice.githubClone.reposViewer.presentation.reposList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkoro.practice.githubClone.core.domain.util.NetworkError
import com.kenkoro.practice.githubClone.core.domain.util.onError
import com.kenkoro.practice.githubClone.core.domain.util.onSuccess
import com.kenkoro.practice.githubClone.reposViewer.domain.Repo
import com.kenkoro.practice.githubClone.reposViewer.domain.ReposViewerRepository
import com.kenkoro.practice.githubClone.reposViewer.presentation.models.RepoUi
import com.kenkoro.practice.githubClone.reposViewer.presentation.models.toRepoUi
import com.kenkoro.practice.githubClone.reposViewer.presentation.reposList.util.ColorProvider
import com.kenkoro.practice.githubClone.reposViewer.presentation.reposList.util.NetworkErrorMessageProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReposListViewModel
  @Inject
  constructor(
    private val appRepository: ReposViewerRepository,
    private val colorProvider: ColorProvider,
    private val errorMessageProvider: NetworkErrorMessageProvider,
  ) : ViewModel() {
    private val _state by lazy {
      MutableLiveData<State>(State.Loading)
    }
    val state: LiveData<State> = _state

    init {
      getRepositories()
    }

    private fun getRepositories() {
      if (state.value !is State.Loading) {
        _state.value = State.Loading
      }

      viewModelScope.launch {
        val result =
          withContext(Dispatchers.IO) {
            appRepository.getRepositories()
          }
        result
          .onSuccess(this@ReposListViewModel::onSuccess)
          .onError(this@ReposListViewModel::onError)
      }
    }

    private fun onSuccess(repos: List<Repo>) {
      _state.value =
        if (repos.isEmpty()) {
          State.Empty
        } else {
          State.Loaded(
            repos.map { repo ->
              repo.toRepoUi(colorProvider.getColor(repo.language))
            },
          )
        }
    }

    private fun onError(networkError: NetworkError) {
      val message = errorMessageProvider.getMessage(networkError)
      _state.value = State.Error(message)
    }

    fun onRetryButtonPressed() {
      getRepositories()
    }

    sealed interface State {
      data object Loading : State

      data class Loaded(val repos: List<RepoUi>) : State

      data class Error(val error: String) : State

      data object Empty : State
    }

    sealed interface Action {
      data object RouteToRepoDetails : Action
    }
  }