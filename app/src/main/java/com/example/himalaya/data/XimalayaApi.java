package com.example.himalaya.data;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {

    private XimalayaApi() { }

    private static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi() {
        if (sXimalayaApi == null) {
            synchronized (XimalayaApi.class) {
                if (sXimalayaApi == null) {
                    sXimalayaApi = new XimalayaApi();
                }
            }
        }
        return sXimalayaApi;
    }

    /**
     * 获取推荐内容
     *
     * @param callback 请求结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, callback);
    }

    /**
     * 根据专辑的ID获取到专辑内容
     *
     * @param albumId 专辑 id
     * @param pageIndex 第几页
     * @param callBack 请求结果的回调接口
     */
    public void getAlbumDetail(long albumId, int pageIndex, IDataCallBack<TrackList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }

    /**
     * 搜索
     * @param keyWord
     * @param page
     * @param callback
     */
    public void searchByKeyWord(String keyWord, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取热词
     * @param callBack
     */
    public void getHotWord(IDataCallBack<HotWordList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(Constants.COUNT_HOT_WORD));
        CommonRequest.getHotWords(map, callBack);
    }

    /**
     * 获取某个关键字的联想词
     * @param keyWord
     * @param callBack
     */
    public void getSuggestWord(String keyWord, IDataCallBack<SuggestWords> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        CommonRequest.getSuggestWord(map, callBack);
    }
}
