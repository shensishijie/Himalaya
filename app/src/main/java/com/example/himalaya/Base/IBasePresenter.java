package com.example.himalaya.Base;

import com.example.himalaya.interfaces.IRecommendViewCallback;

public interface IBasePresenter<Callback>{
    /**
     * 用于注册UI的回调
     * @param callback
     */
    void registerViewCallback(Callback callback);

    /**
     * 取消UI的回调注册
     * @param callback
     */
    void unregisterViewCallback(Callback callback);
}
