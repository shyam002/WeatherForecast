package com.example.administrator.starkspire.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.administrator.weatherforecast.R
import com.example.administrator.weatherforecast.jsonclass.Forecastday
import com.squareup.picasso.Picasso

class WeatherAdapter(internal var context: Context, internal var forecastList: List<Forecastday>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val layoutresource = R.layout.items
        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(layoutresource, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.tempcTV.text = "${forecastList[i].day.avgtempC}℃"
        viewHolder.dateTV.text = "${forecastList[i].date}"
        viewHolder.conditiontextTV.text = "${forecastList[i].day.condition.text}"
        Picasso.with(context).load("https:${forecastList[i].day.condition.icon}").into(viewHolder.imagview)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class ViewHolder internal constructor(convertView: View) : RecyclerView.ViewHolder(convertView) {
        internal var tempcTV: TextView
        internal var conditiontextTV: TextView
        internal var dateTV: TextView
        internal var imagview: ImageView
        init {
            tempcTV = convertView.findViewById(R.id.tempcTV)
            conditiontextTV = convertView.findViewById(R.id.conditiontextTV)
            dateTV = convertView.findViewById(R.id.dateTV)
            imagview = convertView.findViewById(R.id.imagview)
        }
    }

}
