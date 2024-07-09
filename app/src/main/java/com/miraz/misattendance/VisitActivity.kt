package com.miraz.misattendance

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.miraz.misattendance.databinding.ActivityVisitBinding


class VisitActivity : AppCompatActivity() {


    private var refreshRunnable: Runnable? = null
    lateinit var binding: ActivityVisitBinding
    private var countDownTimer: CountDownTimer? = null


    private val handler = Handler(Looper.getMainLooper())
//    private lateinit var refreshRunnable: Runnable
//    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        getSearchData(5)

        binding.imgLogut.setOnClickListener {
            finish()
        }
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { refreshData() })

        // Start periodic refresh
        startPeriodicRefresh();
    }

    private fun startPeriodicRefresh() {
        refreshRunnable = object : Runnable {
            override fun run() {

                refreshData()
                startCountdown()
                handler.postDelayed(this, 20000) // Schedule the next refresh after 10 seconds
            }
        }
        handler.post(refreshRunnable as Runnable) // Start the first refresh
    }

    private fun startCountdown() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        countDownTimer = object : CountDownTimer(18000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.countdownTimer.setText("Next refresh in " + millisUntilFinished / 1000 + "s")
            }

            override fun onFinish() {
                binding.countdownTimer.setText("Refreshing...")
            }
        }
        countDownTimer?.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            refreshRunnable?.let { handler.removeCallbacks(it) }
        }
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }
    private fun refreshData() {
        // Refresh your data here and update the adapter
        Handler().postDelayed(Runnable {
            getSearchData(5)
            binding.swipeRefreshLayout.setRefreshing(false)
        }, 1000) // Simulate a network call with a delay
    }


    private fun getSearchData(number: Int) {
        binding.progressBar.visibility = View.VISIBLE
        ApiServices.visitSearch(number, object : VisitedListener {
            override fun success(isSuccess: Boolean, message: String) {
                binding.progressBar.visibility = View.GONE
                Const().showToast(this@VisitActivity, "$message")
            }

            override fun data(visitedLogRP: VisitedLogRP) {
                if (visitedLogRP.data.isNotEmpty()) {
                    val adapter = VisitAdapter(visitedLogRP.data)
                    binding.recyclerView.adapter = adapter
                    binding.tvNoData.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                    val adapter = VisitAdapter(visitedLogRP.data)
                    binding.recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}