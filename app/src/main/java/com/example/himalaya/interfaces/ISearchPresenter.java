package com.example.himalaya.interfaces;

import com.example.himalaya.Base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    /**
     * 搜索
     * @param keyWord
     */
    void doSearch(String keyWord);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 获取推荐的关键字（相关的关键字）
     * @param keyWord
     */
    void getRecommendWord(String keyWord);


}
