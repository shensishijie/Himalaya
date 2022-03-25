package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    void onPlayStart();

    void onPlayPause();

    void onPlayStop();

    void onPlayError();

    void onNextPlay(Track track);

    void onPrePlay(Track track);

    /**
     * 播放数据加载完成
     * @param list
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式改变
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条的改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(long currentProgress, long total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告加载完成
     */
    void onAdFinish();



}
