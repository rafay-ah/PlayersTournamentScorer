package com.players.nest.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationRequest {

    @Headers({"Content-Type:application/json", "Authorization:key=AAAA8AJ4Ma0:APA91bEXQVZLa3qx5-xjQb8ls4UOHm5X2_xAcrSLebuzpDmtrHA_GmrUtUF58faI1DL0e9TU01qTBwL3y_bePR5hdkhOSAm70mD-7GiiphLnDEpKITkVKF-vD-vabgYKIrqtq-rUQYSU"})
    @POST("send")
    Call<NotificationResponse> sent(@Body NotificationReq notificationRequest);

}
