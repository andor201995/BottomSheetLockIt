package com.andor.bottomsheetlockit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.andor.bottomsheetlockit.core.AppState
import com.andor.bottomsheetlockit.core.BottomMenuState
import com.andor.bottomsheetlockit.core.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MainActivity : AppCompatActivity() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomSheetBehavior =
            BottomSheetBehavior.from(findViewById<View>(R.id.bottomSheetNavHostFragment))
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.hideBottomSheet()
        bindAppStateStream()
    }

    private fun bindAppStateStream() {
        viewModel.getAppStateStream().observe(this, Observer {
            handleAppStateChange(it)
        })
    }

    private fun handleAppStateChange(appState: AppState) {
        if (appState.bottomMenuState is BottomMenuState.Visible) {
            hideSoftKeyboard()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, p1: Int) {
                when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_DRAGGING -> viewModel.showBottomSheet()

                }
            }
        })
    }

    fun hideSoftKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        val navControllerBottomSheet =
            Navigation.findNavController(findViewById(R.id.bottomSheetNavHostFragment))
        if (navControllerBottomSheet.currentDestination!!.id == R.id.bottomMenuFragment && bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            viewModel.hideBottomSheet()
            return
        }
        super.onBackPressed()
    }
}
