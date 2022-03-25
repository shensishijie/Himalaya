package com.example.himalaya.interfaces;

import com.example.himalaya.Base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 播放上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目位置播放
     * @param index 节目在列表中的位置
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     * @param progress
     */
    void seekTp(int progress);

}
