package me.danbeneventano.weather

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Address
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import me.danbeneventano.weather.dataclasses.WeatherRequest

class MainActivity : LifecycleActivity() {

    private lateinit var viewModel: WeatherViewModel
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
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
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

    private fun refresh() {
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
        Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show()
    }
}

fun Double.toPercentageString(): String = "${(this * 100.0).toString().substring(0, 2)}%".replace(".", "")
