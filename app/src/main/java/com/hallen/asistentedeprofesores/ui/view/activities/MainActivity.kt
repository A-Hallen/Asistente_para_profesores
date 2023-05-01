package com.hallen.asistentedeprofesores.ui.view.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.hallen.asistentedeprofesores.Aplication.Companion.prefs
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.ActivityMainBinding
import com.hallen.asistentedeprofesores.databinding.DialogApiKeyBinding
import com.hallen.asistentedeprofesores.ui.view.fragments.FragmentGroup
import com.hallen.asistentedeprofesores.ui.viewmodel.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val groupViewModel: GroupViewModel by viewModels()
    private lateinit var navController: NavController

    fun getToolbar(): Toolbar {
        return binding.toolbar
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // Set the toolbar as the support action bar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.root)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)

        // Add listener for the floating action button
        binding.newClase.setOnClickListener {
            createGroup()
        }

        binding.navigationView.setNavigationItemSelectedListener {
            val bundle = Bundle().apply { putInt("groupId", it.itemId) }
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeViewPagerFragment, false).build()

            if (it.itemId != navController.currentDestination?.id){
                if (it.itemId == R.id.openAiFragment){
                    checkApiKey(bundle, navOptions)
                    return@setNavigationItemSelectedListener false
                } else{
                    binding.navigationView.setCheckedItem(it.itemId)
                    binding.root.closeDrawer(GravityCompat.START, false)
                    navController.navigate(it.itemId, bundle, navOptions)
                }
            }
            true //it.onNavDestinationSelected(navController)
        }

        groupViewModel.groupModel.observe(this){
            val menu = binding.navigationView.menu
            menu.clear()
            binding.navigationView.inflateMenu(R.menu.drawer_items)

            if (it.isEmpty()) return@observe
            val subMenu = menu.addSubMenu("Grupos: ")

            for(group in it){
                subMenu.add(Menu.NONE, group.id, Menu.CATEGORY_CONTAINER, group.name)
                subMenu.setGroupCheckable(Menu.NONE, true, false)

                val destination = navController.navigatorProvider.getNavigator(FragmentNavigator::class.java).createDestination()
                destination.id = group.id
                destination.label = group.name
                destination.setClassName(FragmentGroup::class.java.name)
                navController.graph.addDestination(destination)
            }
        }
        groupViewModel.getGroups()
    }

    private fun checkApiKey(bundle: Bundle, navOptions: NavOptions) {
        val apiKey = prefs.getOpenAIKey()
        if (apiKey.isNotBlank()){
            binding.navigationView.setCheckedItem(R.id.openAiFragment)
            binding.root.closeDrawer(GravityCompat.START, false)
            navController.navigate(R.id.openAiFragment, bundle, navOptions)
        } else askApiKey(bundle, navOptions)
    }

    private fun askApiKey(bundle: Bundle, navOptions: NavOptions) {
        val dialog = Dialog(this)
        val dialogBinding = DialogApiKeyBinding.inflate(dialog.layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window!!.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window!!.attributes = layoutParams
        }
        with(dialogBinding){
            apiKeyEdit.hint = "Openai Key"
            guardar.setOnClickListener {
                val key = apiKeyEdit.text.toString()
                if (key.isNotBlank()){
                    prefs.saveOpenAIKey(key)
                    checkApiKey(bundle, navOptions)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.root)
    }

    override fun onBackPressed() {
        if (binding.root.isDrawerOpen(GravityCompat.START)) {
            binding.root.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Esta funcion nos lleva hacia un Fragmento para crear el grupo.
     */
    private fun createGroup() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeViewPagerFragment, false).build()
        navController.navigate(R.id.fragmentCreateGroup, null, navOptions)
    }

}