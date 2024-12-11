package com.kenkoro.practice.githubClone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.kenkoro.practice.githubClone.core.navigation.Screen
import com.kenkoro.practice.githubClone.reposViewer.data.storage.KeyValueStorage
import com.kenkoro.practice.githubClone.reposViewer.presentation.auth.AuthFragment
import com.kenkoro.practice.githubClone.reposViewer.presentation.reposList.ReposListFragment
import com.kenkoro.projects.githubClone.R
import com.kenkoro.projects.githubClone.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  @Inject
  lateinit var keyValueStorage: KeyValueStorage

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupNavigation()
  }

  private fun setupNavigation() {
    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
    val navController = navHostFragment.navController
    val startDestination = getStartDestination().route
    navController.graph =
      navController.createGraph(
        startDestination = startDestination,
      ) {
        fragment<AuthFragment>(route = Screen.Auth.route) { label = "Auth Screen" }
        fragment<ReposListFragment>(route = Screen.ReposList.route) { label = "Repos List Screen" }
      }
  }

  private fun getStartDestination(): Screen {
    val token = keyValueStorage.retrieveToken()
    return if (token.isBlank()) Screen.Auth else Screen.ReposList
  }
}