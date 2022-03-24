package com.example.himalaya.interfaces;

public interface IAlbumDetailPresenter {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);

    /**
     * 用于注册UI的回调
     * @param detailViewCallback
     */
    void registerViewCallback(IAlbumDetailViewCallback detailViewCallback);

    /**
     * 取消UI的回调注册
     * @param detailViewCallback
     */
    void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback);
}

