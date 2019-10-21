package com.cjun.app.schultegrid

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private var tvCounter: TextView? = null
    private var rvGrid: RecyclerView? = null
    private var tvTimer: TextView? = null
    private var btnStart: Button? = null
    private var sbSize: SeekBar? = null
    private var tvSize: TextView? = null
    private var cbColour: CheckBox? = null
    private var lvRank: ListView? = null

    private var gridAdapter: GridAdapter? = null
    private var rankAdapter: ArrayAdapter<String>? = null


    private val handler: Handler = Handler()
    private var size: Int = 2
    private var second: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        tvTimer = findViewById(R.id.tv_timer)
        tvCounter = findViewById(R.id.tv_counter)
        rvGrid = findViewById(R.id.rv_grid)
        tvSize = findViewById(R.id.tv_size)
        lvRank = findViewById(R.id.lv_rank)
        rankAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        lvRank?.adapter = rankAdapter

        cbColour = findViewById(R.id.cb_colour)
        cbColour?.setOnCheckedChangeListener { _: View, isCheck: Boolean ->
            gridAdapter?.colour = isCheck
        }

        sbSize = findViewById(R.id.sb_size)
        sbSize?.max = 8
        sbSize?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSize?.text = (progress + 2).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                size = seekBar?.progress ?: 0
                size += 2
                gridAdapter?.size = size
                val gridLayoutManager = rvGrid?.layoutManager as GridLayoutManager
                gridLayoutManager.spanCount = size
                gridAdapter?.reset()
                gridAdapter?.notifyDataSetChanged()
            }
        })

        rvGrid?.layoutManager = GridLayoutManager(this, size)
        gridAdapter = GridAdapter(size)
        gridAdapter?.listener = object : GridAdapter.OnGridListener {
            override fun onComplete() {
                handler.removeCallbacks(updateTimerTask)
                gridAdapter?.start = false
                val min = second / 60
                val sec = second % 60
                rankAdapter?.add("$size×$size  ${String.format("%02d:%02d", min, sec)}")
            }

            override fun onUpdateCounter(count: Int) {
                tvCounter?.text = "当前 : $count"
            }
        }
        rvGrid?.adapter = gridAdapter

        rvGrid?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvGrid?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        rvGrid?.setFadingEdgeLength(0)

        btnStart = findViewById(R.id.btn_start)
        btnStart?.setOnClickListener {
            start()
        }
    }

    private fun sortRank() {
        rankAdapter?.sort { s1: String?, s2: String? ->
            s1?.compareTo(s2 ?: "") ?: 0
        }
    }

    private fun start() {
        gridAdapter?.show = true
        gridAdapter?.reset()
        gridAdapter?.notifyDataSetChanged()
        gridAdapter?.start = true
        btnStart?.text = "重置"
        tvCounter?.text = ""
        second = 0


        handler.removeCallbacks(updateTimerTask)
        handler.post(updateTimerTask)
    }

    private val updateTimerTask = object : Runnable {
        override fun run() {
            val min = second / 60
            val sec = second % 60
            tvTimer?.text = String.format("%02d:%02d", min, sec)
            handler.postDelayed(this, 1000)
            second++
        }
    }
}