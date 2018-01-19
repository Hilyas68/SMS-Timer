package com.hcodestudio.smstimer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.hcodestudio.smstimer.utils.NotificationUtils;

/**
 * Created by hassan on 12/27/2017.
 */

public class MessageScheduler extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleMessageActivity.sendMessage(context);
        NotificationUtils.remindUser(context);
    }
}
