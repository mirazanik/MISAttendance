package com.miraz.misattendance

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
        holder.tvLastVisitedDate.text = userDetails.last_visited_date

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
        dialogStdRoll.text = userDetails.last_visited_date
        dialog.show()
    }
}