package com.example.himalaya;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.himalaya.Base.BaseActivity;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.presenters.SearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.FlowTextLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

    private static final String TAG = "SearchActivity";
    private View mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }

    private void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
        mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
    }

    private void initEvent() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:去执行搜索的逻辑

            }
        });

        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG, "content -- >" + s);
                LogUtil.d(TAG, "start -- >" + start);
                LogUtil.d(TAG, "before -- >" + before);
                LogUtil.d(TAG, "count -- >" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        LogUtil.d(TAG, "size -- >" + hotWordList.size());
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            hotWords.add(hotWord.getSearchword());
        }
        Collections.sort(hotWords);
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> list, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
            mSearchPresenter = null;
        }
    }
}