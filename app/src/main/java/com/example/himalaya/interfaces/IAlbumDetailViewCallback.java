package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    /**
     * 专辑详情内容加载出来了
     * @param trackList
     */
    void onDetailListLoaded(List<Track> trackList);

    /**
     * 把album传给UI使用
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 网络错误
     */
    void onNetworkError();


    /**
     * 加载更多的结果
     * @param size size > 0 表示加载成功, 否则表示加载失败
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     * @param size
     */
    void onRefreshFinished(int size);

}
