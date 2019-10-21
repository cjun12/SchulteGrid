package com.cjun.app.schultegrid

import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.random.Random

class GridAdapter() : RecyclerView.Adapter<GridAdapter.Holder>() {
    var size: Int = 2
        set(value) {
            field = value
            initArray()
            refreshArray()
            notifyDataSetChanged()
        }

    private var random: Random = Random(Date().time)

    private var array: Array<IntArray>? = null
    private var count: Int = 0
    var listener: OnGridListener? = null

    var show: Boolean = false
    var start: Boolean = false
    var colour: Boolean = false

    private fun initArray() {
        array = Array(size) { IntArray(size) }
        for (i in 0 until size) {
            for (j in 0 until size) {
                array!![i][j] = i * size + j + 1
            }
        }
    }

    private fun refreshArray() {
        val num: Int = size * size
        for (times in 1..num) {
            val i = random.nextInt(num)
            val j = random.nextInt(num)
            val temp = array!![i / size][i % size]
            array!![i / size][i % size] = array!![j / size][j % size]
            array!![j / size][j % size] = temp
        }
    }

    init {
        initArray()
        refreshArray()
    }

    constructor(size: Int) : this() {
        this.size = size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val textView = TextViewEx(parent.context)
        val length = parent.measuredWidth / size
        val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, length)
        textView.layoutParams = layoutParams
        textView.gravity = Gravity.CENTER
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            textView.background = parent.resources.getDrawable(R.drawable.common_border, null)
//        }
        textView.setBackgroundColor(Color.WHITE)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)

        textView.setOnClickListener {
            if (!start) {
                return@setOnClickListener
            }
            if (it is TextView) {
                val numStr = it.text.toString()
                if (!TextUtils.isEmpty(numStr) && numStr.toInt() == count + 1) {
                    if (colour) {
                        it.setBackgroundColor(0xff000000.toInt() or random.nextInt(0xffffff))
                    }
                    if (++count == size * size) {
                        listener?.onComplete()
                    }
                    listener?.onUpdateCounter(count)
                }
            }
        }
        return Holder(textView)
    }

    override fun getItemCount(): Int {
        return size * size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (show) {
            if (holder.itemView is TextView) {
                holder.itemView.setBackgroundColor(Color.WHITE)
                holder.itemView.text = array!![position / size][position % size].toString()
            }
        }
    }

    fun reset() {
        count = 0
        refreshArray()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
    interface OnGridListener {
        fun onComplete()
        fun onUpdateCounter(count: Int)
    }
}
