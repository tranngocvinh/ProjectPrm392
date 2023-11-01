package com.example.projectprm392.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.projectprm392.constant.Constant;
import com.example.projectprm392.constant.GlobalFuntion;

public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getExtras().getInt(Constant.MUSIC_ACTION);
        GlobalFuntion.startMusicService(context, action, com.example.projectprm392.service.MusicService.mSongPosition);
    }
}