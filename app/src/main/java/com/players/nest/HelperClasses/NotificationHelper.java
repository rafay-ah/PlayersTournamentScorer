package com.players.nest.HelperClasses;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.players.nest.ModelClasses.User;
import com.players.nest.Notifications.NotificationReq;
import com.players.nest.Notifications.NotificationRequest;
import com.players.nest.Notifications.NotificationResponse;
import com.players.nest.Notifications.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.players.nest.ProfileFragment.ViewProfileFragment.VIEW_PROFILE_TYPE;

public class NotificationHelper {


    public void sendNotification(Context context, String deviceToken, String notificationTitle, String notificationBody,
                                 String clickAction, NotificationReq.Data data) {

        NotificationReq notificationReq;

        if (clickAction != null) {
            notificationReq = new NotificationReq(deviceToken,
                    new NotificationReq.Notification(notificationTitle, notificationBody, clickAction,
                            Constants.NOTIFICATION_SOUND),
                    data);
        } else
            notificationReq = new NotificationReq(deviceToken,
                    new NotificationReq.Notification(notificationTitle, notificationBody, "",
                            Constants.NOTIFICATION_SOUND),
                    null);

        RetrofitHelper.getRetrofit(Constants.BASE_URL)
                .create(NotificationRequest.class)
                .sent(notificationReq)
                .enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                        if (response.code() == 200) {
                            //Awesome
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void sendNotificationsForChatFragment(Context context, User currentUser, User user, String msg) {

        NotificationReq notificationReq = new NotificationReq(user.getDeviceToken(),
                new NotificationReq.Notification(currentUser.getUsername(), msg, "Chat_Fragment", Constants.NOTIFICATION_SOUND),
                new NotificationReq.Data(VIEW_PROFILE_TYPE, currentUser));

        RetrofitHelper.getRetrofit(Constants.BASE_URL)
                .create(NotificationRequest.class)
                .sent(notificationReq)
                .enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                        if (response.code() == 200) {
                            //Awesome
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}