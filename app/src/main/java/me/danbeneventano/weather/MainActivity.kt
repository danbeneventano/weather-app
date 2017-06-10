package me.danbeneventano.weather

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_location.*
import me.danbeneventano.weather.dataclasses.WeatherRequest

class MainActivity : LifecycleActivity() {

    lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: RecyclerAdapter
    private lateinit var address: Address
    private lateinit var cm: ConnectivityManager
    private var isRecyclerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cm = appKodein().instance()
        setActionBar(toolbar)
        swipe_refresh_layout.setOnRefreshListener(this::refresh)

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            refresh()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                refresh()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.search_location -> {
            LocationDialog(this).show(fragmentManager, "Search Location")
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(location_edittext, InputMethodManager.SHOW_IMPLICIT)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun initializeRecycler() {
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        viewModel.inject(appKodein())
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = FadeInAnimator()
        recycler.itemAnimator.apply {
            addDuration = 500
            changeDuration = 500
            moveDuration = 500
            removeDuration = 500
        }
        progress_bar.visibility = View.GONE
        recycler.visibility = View.VISIBLE
        viewModel.location.observe(this, Observer {
            address = it?.address!!
            viewModel.init(WeatherRequest(latitude = it.latitude.toString(), longitude = it.longitude.toString()))
            viewModel.weather.observe(this, Observer {
                adapter = RecyclerAdapter(it!!.daily!!, this, address)
                val animatedAdapter = ScaleInAnimationAdapter(adapter)
                animatedAdapter.setDuration(500)
                animatedAdapter.setFirstOnly(false)
                val alphaAdapter = AlphaInAnimationAdapter(animatedAdapter)
                alphaAdapter.setDuration(500)
                alphaAdapter.setFirstOnly(false)
                recycler.adapter = animatedAdapter
                adapter.daily.data.forEachIndexed { index, _ -> adapter.notifyItemChanged(index) }
            })
        })
        isRecyclerInitialized = true
    }

    fun refresh() {
        if (hasConnection()) {
            if (!isRecyclerInitialized) {
                initializeRecycler()
            } else {
                viewModel.location.observe(this, Observer {
                    address = it?.address!!
                    viewModel.init(WeatherRequest(latitude = it.latitude.toString(), longitude = it.longitude.toString()))
                    viewModel.weather.observe(this, Observer {
                        adapter.daily = it!!.daily!!
                        adapter.address = address
                        adapter.daily.data.forEachIndexed { index, _ -> adapter.notifyItemChanged(index) }
                        swipe_refresh_layout.isRefreshing = false
                    })
                })
            }
        } else {
            handleNoConnection()
        }
    }

    private fun hasConnection(): Boolean {
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun handleNoConnection() {
        progress_bar.visibility = View.VISIBLE
        recycler.visibility = View.GONE
        swipe_refresh_layout.isRefreshing = false
        Snackbar.make(main_linear_layout, "No Connection", Snackbar.LENGTH_LONG).show()
    }
}

fun Double.toPercentageString(): String = "${(this * 100.0).toString().substring(0, 2)}%".replace(".", "")
