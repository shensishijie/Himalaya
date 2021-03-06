package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;
    private List<Album> mRecommendList;

    private RecommendPresenter() {}

    private static RecommendPresenter sInstance = null;

    /**
     *获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if(sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getRecommendList() {
        /**获取推荐内容，其实就是猜你喜欢
         * 3.10.6，获取猜你喜欢专辑
         */
        //如果内容不空的话，那么直接使用当前的内容
        if(mRecommendList != null && mRecommendList.size() > 0) {
            LogUtil.d(TAG,"getRecommendList -- > from list.");
            handlerRecommendResult(mRecommendList);
            return;
        }
        loadRecommendListFormNetwork();
    }

    private void loadRecommendListFormNetwork() {
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG, "thread name -- > " + Thread.currentThread().getName());
                if (gussLikeAlbumList != null) {
                    mRecommendList = gussLikeAlbumList.getAlbumList();
                    if (mRecommendList != null) {
                        //数据回来以后更新UI
                        handlerRecommendResult(mRecommendList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error --> " + i);
                LogUtil.d(TAG, "errorMsg --> " + s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
            if(albumList.size() == 0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            } else {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    /**
     * 获取当前的推荐列表
     *
     * @return 推荐专辑列表，使用前需要判空
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }

    private void updateLoading() {
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void pull2RefreshMore() {
        loadRecommendListFormNetwork();
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
