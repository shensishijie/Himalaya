package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {

    /**
     * 添加后通知UI
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除后通知UI
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 订阅专辑加载的回调结果
     * @param albums
     */
    void onSubscriptionLoaded(List<Album> albums);

    /**
     * 订阅数量满了
     */
    void onSubFull();
}
