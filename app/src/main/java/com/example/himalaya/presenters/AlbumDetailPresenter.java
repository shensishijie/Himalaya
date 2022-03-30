package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    private Album mTargetAlbum = null;
    //当前页
    private int mCurrentPageIndex = 0;
    //当前专辑id
    private int mCurrentAlbumId = -1;
    //当前列表
    List<Track> mTracks = new ArrayList<>();

    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //加载更多内容
        mCurrentPageIndex++;
        doLoaded(true);
    }

    private void doLoaded(boolean isLoaderMore) {
        //根据页码和id获取内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(mCurrentAlbumId, mCurrentPageIndex, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks size -- >" + tracks.size());
                    if (isLoaderMore) {
                        //上拉加载，结果放到后面
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    } else {
                        //下拉加载，结果放到前面
                        mTracks.addAll(0,tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                mCurrentPageIndex--;
                LogUtil.d(TAG, "errorCode -- >" + errorCode);
                LogUtil.d(TAG, "errorMsg -- >" + errorMsg);
                handlerError(errorCode, errorMsg);
            }
        });
    }

    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        doLoaded(false);
    }

    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onNetworkError();
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(detailViewCallback);
        }
    }

    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
