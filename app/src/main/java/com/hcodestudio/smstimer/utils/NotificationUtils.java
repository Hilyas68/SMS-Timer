package com.hcodestudio.smstimer.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.hcodestudio.smstimer.R;
import com.hcodestudio.smstimer.activities.MainActivity;

/**
 * Created by hassan on 12/27/2017.
 */

public class NotificationUtils extends AppCompatActivity {
    private static final int PENDING_INTENT_ID =3417;
    private static final int NOTIFICATION_ID =1138;


    public static void remindUser(Context context ) {


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_message_small)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("SMS Timer")
                .setContentText("Message Sent Successfully")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("your message has been sent"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID , notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){

        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_message_black_24dp);
        return largeIcon;
    }
}

