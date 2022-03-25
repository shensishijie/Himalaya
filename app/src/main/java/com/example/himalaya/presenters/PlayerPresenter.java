package com.example.himalaya.presenters;

import com.example.himalaya.Base.BaseActivity;
import com.example.himalaya.Base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class PlayerPresenter implements IPlayerPresenter {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
    };

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                sPlayerPresenter = new PlayerPresenter();
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
        } else {
            LogUtil.d(TAG, "mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTp(int progress) {

    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {

    }

    @Override
    public void unregisterViewCallback(IPlayerCallback iPlayerCallback) {

    }
}
