package team.plexet.mobile.service.net

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import team.plexet.mobile.service.net.dto.DataDTO

interface RestApi {
    @POST("/public/api/point/collect")
    fun postData(@Body data: DataDTO): Call<ResponseBody>
}