package com.example.catherine.bfalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        Toast.makeText(context, "闹钟响了", Toast.LENGTH_LONG).show();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

}