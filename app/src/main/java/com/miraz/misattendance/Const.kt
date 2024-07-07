package com.miraz.misattendance

import android.content.Context
import android.view.Gravity
import android.widget.Toast


/**
 * Created by Md Miraz Hossain on 12-Jul-23.
 * miraz.anik@gmail.com
 */

class Const {
    companion object {
        var toast: Toast? = null
        var TOKEN = ""
        var JOBTYPEID :String= ""
        var autoSearch : Boolean =false
        var TOCKENAIZER = "###"
    }

    fun showToast(context: Context, message: String) {
        toast?.cancel() // Cancel previous toast if it's still showing
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.CENTER, 0, 250);
        toast?.show()
    }
    fun showToast(message: String, context: Context) {
        toast?.cancel() // Cancel previous toast if it's still showing
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}