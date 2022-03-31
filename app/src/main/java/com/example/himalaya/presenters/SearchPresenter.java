package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.SearchActivity;
import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    private List<ISearchCallback> mCallbacks = new ArrayList<>();

    //当前的搜索关键字
    private String mCurrentKeyWord = null;
    private XimalayaApi mXimalayaApi;

    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter() {
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private volatile static SearchPresenter sSearchPresenter;

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyWord) {
        //用于重新搜索
        //网络不佳时用户会点击重新搜索
        this.mCurrentKeyWord = keyWord;
        search(keyWord);
    }

    private void search(String keyWord) {
        mXimalayaApi.searchByKeyWord(keyWord, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                } else {
                    LogUtil.d(TAG, "albums is null.");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyWord);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWord(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                List<HotWord> hotWords = hotWordList.getHotWordList();
                if (hotWords != null) {
                    LogUtil.d(TAG, "hotWords size -- > " + hotWords.size());
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onHotWordLoaded(hotWords);
                    }
                } else {
                    LogUtil.d(TAG, "hotWords is null. ");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyWord) {
        mXimalayaApi.getSuggestWord(keyWord, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                if (keyWordList != null) {
                    LogUtil.d(TAG, "keyWordList size -- > " + keyWordList.size());
                } else {
                    LogUtil.d(TAG, "keyWordList is null -- > ");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unregisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
