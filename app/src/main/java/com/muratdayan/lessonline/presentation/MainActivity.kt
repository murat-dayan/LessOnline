package com.muratdayan.lessonline.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
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

    private val loginViewModel:LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView){v,insets->
            v.setPadding(0, 0, 0, 0)
            insets
        }



        navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        loginViewModel.checkIfUserLoggedIn()

        lifecycleScope.launch {
            loginViewModel.loginState.collectLatest {state->
                when(state){
                    is LoginState.Success->{
                        navController.navigate(R.id.homeFragment)
                    }
                    else->{
                        navController.navigate(R.id.loginFragment)
                    }
                }
            }
        }

        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.loginFragment, R.id.registerFragment, R.id.getProfileInfoFragment,R.id.forgetPasswordFragment, R.id.editPostFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()

    }
}