package com.muratdayan.lessonline.presentation.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.ActivityMainBinding
import com.muratdayan.lessonline.presentation.features.auth.login.LoginState
import com.muratdayan.lessonline.presentation.features.auth.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val loginViewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        supportActionBar?.hide()

        navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        mainViewModel.checkUserRole()
        mainViewModel.checkAuthUser()


        lifecycleScope.launch {
            loginViewModel.loginState.collectLatest { state ->
                when (state) {
                    is LoginState.Success -> {
                        navController.navigate(R.id.homeFragment)
                    }

                    else -> {
                        navController.navigate(R.id.loginFragment)
                    }
                }
            }
        }

        mainViewModel.isTeacher.observe(this){isTeacher->
            Log.d("MainActivity","isTeacher-> $isTeacher")
            val menu = binding.bottomNavigationView.menu
            val addPostMenuItem = menu.findItem(R.id.addPostFragment)
            addPostMenuItem.isVisible = isTeacher
            binding.bottomNavigationView.invalidate()
        }

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            listenForNotifications(currentUserId, binding.bottomNavigationView)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.getProfileInfoFragment, R.id.forgetPasswordFragment, R.id.editPostFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        // Giriş durumu kontrolü
        if (mainViewModel.isUserLoggedIn()) {
            // Kullanıcı giriş yaptıysa, HomeFragment'a git
            navController.navigate(R.id.homeFragment)
            navController.popBackStack(R.id.loginFragment, true)
        } else {
            // Kullanıcı giriş yapmadıysa, LoginFragment'a git
            navController.navigate(R.id.loginFragment)
        }

        // OnBackPressedDispatcher ile geri tuşunu kontrol et
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestination = navController.currentDestination?.id

                // Eğer HomeFragment'taysak geri tuşuna basıldığında hiçbir şey yapma
                if (currentDestination == R.id.homeFragment) {
                    isEnabled = false // Bu callback'i devre dışı bırak
                } else {
                    navController.popBackStack() // Normal geri tuşu davranışını uygula
                }
            }
        })

    }

    private fun listenForNotifications(
        currentUserId: String,
        bottomNavigationView: BottomNavigationView
    ) {
        FirebaseFirestore.getInstance().collection("users").document(currentUserId)
            .collection("notifications")
            .addSnapshotListener { snapshot, error ->
                val notificationCount = snapshot?.size() ?: 0
                val badge = bottomNavigationView.getOrCreateBadge(R.id.answernotificationsfragment)
                if (notificationCount > 0) {
                    badge.number = notificationCount
                    badge.isVisible = true
                } else {
                    badge.isVisible = false
                }
            }
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}