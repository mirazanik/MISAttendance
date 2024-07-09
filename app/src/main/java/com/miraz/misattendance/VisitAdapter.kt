package com.miraz.misattendance

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class VisitAdapter(val userSearchRP: List<DataXXXX>) :
    RecyclerView.Adapter<VisitAdapter.ViewHolder>() {

    lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCustomerName = itemView.findViewById<TextView>(R.id.tvCustomerName)
        var tvLastVisitedDate = itemView.findViewById<TextView>(R.id.tvCustomerLast)
        var userImage = itemView.findViewById<ImageView>(R.id.ivImage)
        var cardView = itemView.findViewById<CardView>(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_visit_result, parent, false)
        context = parent.context


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userSearchRP.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userDetails = userSearchRP[position]

        holder.tvCustomerName.text = userDetails.customer_id

        try {
            holder.tvLastVisitedDate.text = timeDifference(userDetails.last_visited_date)
        } catch (e: Exception) {
            Log.e("TAG", "onBindViewHolder: $e")
            holder.tvLastVisitedDate.text = "Invalid Date"
        }

        holder.cardView.setOnClickListener {
            showDialog(userDetails)
        }


        Picasso.get().load(userDetails.image_url).into(holder.userImage);


    }

    private fun showDialog(userDetails: DataXXXX) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_user_info)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val dialogImageView = dialog.findViewById<ImageView>(R.id.dialogimageView)
        val dialogStdName = dialog.findViewById<TextView>(R.id.tvUserName)
        val dialogStdRoll = dialog.findViewById<TextView>(R.id.tvLastVisitedDate)
        val createJobActivity = dialog.findViewById<Button>(R.id.btnCreateJob)

        dialog.findViewById<View>(R.id.ivClose).setOnClickListener {
            dialog.cancel()
        }

        createJobActivity.setOnClickListener { }


        val url = userDetails.image_url
        Picasso.get().load(url).placeholder(R.drawable.images).into(dialogImageView)
        dialogStdName.text = userDetails.customer_id
        dialogStdRoll.text = timeDifference(userDetails.last_visited_date)
        dialog.show()
    }

    fun timeDifference(targetDateTime: String): String {
        // Adjust the date format to match the provided date string without nanoseconds
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        var target: Date? = null
        try {
            val trimmedDateTime = targetDateTime.substring(0, targetDateTime.indexOf('.') + 4) + "Z"
            target = formatter.parse(trimmedDateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (target == null) {
            return "Invalid date format"
        }

        val now = Date()

        val diffInMillis = now.time - target.time

        val years = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 365
        val remainingDaysAfterYears = TimeUnit.MILLISECONDS.toDays(diffInMillis) % 365

        val months = remainingDaysAfterYears / 30
        val remainingDaysAfterMonths = remainingDaysAfterYears % 30

        val days = remainingDaysAfterMonths

        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60

        val result = StringBuilder()

        if (years > 0) {
            result.append("$years years, ")
        }
        if (months > 0) {
            result.append("$months months, ")
        }
        if (days > 0) {
            result.append("$days days, ")
        }
        if (hours > 0) {
            result.append("$hours hours, ")
        }
        if (minutes > 0) {
            result.append("$minutes minutes")
        }

        // Remove trailing comma and space if they exist
        if (result.endsWith(", ")) {
            result.setLength(result.length - 2)
        }

        return if (result.isEmpty()) "Last visit 0 minutes ago" else "Last visit $result ago"
    }

}