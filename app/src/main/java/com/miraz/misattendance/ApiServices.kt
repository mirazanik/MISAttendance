package com.miraz.misattendance

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


object ApiServices {

    private val TAG = "ApiServices"

    fun visitSearch(
        searchString: Int, searchListener: VisitedListener
    ) {
        val service: ApiInterface =
            RetrofitHelper.getInstance("http://116.68.205.78:7000/api/v1/")
                .create(ApiInterface::class.java)
        //service.visitedLog(searchString)
        service.visitedLog()
            .enqueue(object :
                Callback<VisitedLogRP> {
                override fun onResponse(
                    call: Call<VisitedLogRP>,
                    response: Response<VisitedLogRP>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        searchListener.data(response.body()!!)
                    } else {
                        if (response.code() == 401) {
                            val errorBody = response.errorBody()?.string()
                            try {
                                val errorJson = errorBody?.let { JSONObject(it) }
                                val errorMessage = errorJson?.getString("detail")
                                searchListener.success(false, "Login failed! $errorMessage")
                            } catch (e: JSONException) {
                                searchListener.success(
                                    false,
                                    "Login failed! Unable to parse error message."
                                )
                            }
                        } else {
                            searchListener.success(false, "Login failed!")
                        }
                    }
                }

                override fun onFailure(call: Call<VisitedLogRP>, t: Throwable) {
                    searchListener.success(false, "Request failed!")
                }

            })
    }

    fun uploadImageEmbToAIServer(
        uploadImageAIServerREQ: UploadImageAIServerREQ,
        uploadImageEmbListener: UploadImageEmbListener
    ) {

        val idPart =
            RequestBody.create("text/plain".toMediaTypeOrNull(), uploadImageAIServerREQ.staff_id)
        val namePart =
            RequestBody.create("text/plain".toMediaTypeOrNull(), uploadImageAIServerREQ.name)
        val filePart = RequestBody.create("image/jpg".toMediaType(), uploadImageAIServerREQ.image)
        val department =
            RequestBody.create("text/plain".toMediaTypeOrNull(), uploadImageAIServerREQ.department)
        val designation =
            RequestBody.create("text/plain".toMediaTypeOrNull(), uploadImageAIServerREQ.designation)
        val file = MultipartBody.Part.createFormData(
            "image",
            uploadImageAIServerREQ.image.name, filePart
        )


        val service: ApiInterface =
            RetrofitHelper.getInstance("http://192.168.101.230:6970/")
                .create(ApiInterface::class.java)
        service.registerUser(namePart, idPart, department, designation, file)
            .enqueue(object :
                Callback<RequestBody> {
                override fun onResponse(
                    call: Call<RequestBody>,
                    response: Response<RequestBody>,
                ) {
                    val errorBody = response.errorBody()?.string()
                    try {
                        val errorJson = errorBody?.let { JSONObject(it) }
                        uploadImageEmbListener.success(false, "failed! $errorJson")
                    } catch (e: JSONException) {
                        uploadImageEmbListener.success(
                            false,
                            "Unable to parse error message."
                        )
                    }
                }

                override fun onFailure(call: Call<RequestBody>, t: Throwable) {
                    if (t is HttpException) {
                        val errorBody = t.response()?.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                uploadImageEmbListener.success(
                                    false,
                                    "Error JSON: $jsonObject"
                                )
                                Log.d("UPLOAD", "Error JSON: $jsonObject")
                            } catch (e: Exception) {
                                Log.e("UPLOAD", "Error parsing JSON in onFailure", e)
                                uploadImageEmbListener.success(
                                    false,
                                    e.toString()
                                )
                            }
                        }
                    }

                }
            })
    }

}