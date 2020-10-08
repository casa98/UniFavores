package com.cagudeloa.unifavores.`interface`

import com.cagudeloa.unifavores.constants.Constants.Companion.CONTENT_TYPE
import com.cagudeloa.unifavores.constants.Constants.Companion.SERVER_KEY
import com.cagudeloa.unifavores.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization key: $SERVER_KEY\nContent type: $CONTENT_TYPE")
    @POST("fcm/send")

    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}