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
}
