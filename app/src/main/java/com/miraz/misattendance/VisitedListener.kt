package com.miraz.misattendance

/**
 * Created by Md Miraz Hossain on 18-Feb-24.
 * miraz.anik@gmail.com
 */

interface VisitedListener {
    fun success(isSuccess: Boolean, message: String)
    fun data(visitedLogRP: VisitedLogRP)
}