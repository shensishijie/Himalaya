package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    /**
     * 搜索结果的回调方法
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的回调方法
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     * @param list 结果
     * @param isOkay true表示成功加载更多，false表示没有更多
     */
    void onLoadMoreResult(List<Album> list, boolean isOkay);

    /**
     * 联想关键字的结果回调方法
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 错误回调
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, String errorMsg);
}
