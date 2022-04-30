package com.example.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.himalaya.Base.BaseActivity;
import com.example.himalaya.Base.BaseApplication;
import com.example.himalaya.adapters.TrackListAdapter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.presenters.SubscriptionPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.RoundRectImageView;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, TrackListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;
    private TrackListAdapter mTrackListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mIsLoaderMore = false;
    private String mCurrentTrackTitle;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        initPresenter();
        //设置订阅按钮的状态
        updateSubSate();
        updatePlaySate(mPlayerPresenter.isPlaying());
        initListener();
    }

    private void updateSubSate() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        //这个是专辑详情的presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //这个是播放器的presenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //这个是订阅的presenter
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }

    /**
     * 根据播放状态修改图标和文字
     * @param playing
     */
    private void updatePlaySate(boolean playing) {
        mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
        if (!playing) {
            mPlayControlTips.setText(R.string.click_play_tips_text);
        } else {
            mPlayControlTips.setText(mCurrentTrackTitle);
        }
    }

    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //判断播放器是否有播放列表
                    if (mPlayerPresenter.hasPlayList()) {
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }
                }
            }
        });

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    //如果没有订阅，就去订阅，如果已经订阅了，那么就取消订阅
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    //当播放器没有播放列表时，进行处理
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }

        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.iv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);
        //控制播放的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);

        mSubBtn = this.findViewById(R.id.detail_sub_btn);

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);

        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        mTrackListAdapter = new TrackListAdapter();
        mDetailList.setAdapter(mTrackListAdapter);
        //设置item的上下间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        mTrackListAdapter.setItemClickListener(this);

        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setHeaderHeight(100);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefreshing();
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //去加载更多内容
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore = true;
                }
//                BaseApplication.getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(DetailActivity.this, "加载更多完成..", Toast.LENGTH_SHORT).show();
//                        mRefreshLayout.finishLoadmore();
//                    }
//                }, 2000);
            }
        });

        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> trackList) {
        if (mRefreshLayout != null && mIsLoaderMore) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }

        this.mCurrentTracks = trackList;
        //更新，设置UI数据
        mTrackListAdapter.setData(trackList);

        if (trackList == null || trackList.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;
        long id = album.getId();
        LogUtil.d(TAG, "album -- >" + id);
        mCurrentId = id;
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) id, mCurrentPage);
        }
        //拿数据过程中显示加载中
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (mLargeCover != null) {
            Glide.with(this).load(album.getCoverUrlLarge())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            BaseApplication.getHandler().post(() -> ImageBlur.makeBlur(DetailActivity.this, resource, mLargeCover));
                            return true;
                        }
                    }).submit();
        }
        if (mSmallCover != null) {
            Glide.with(this).load(album.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    @Override
    public void onNetworkError() {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多节目了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        //用户网络不佳点击重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        //跳转到播放器界面
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        //修改图标和文字
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
        updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {
        updatePlaySate(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(trackTitle)) {
                this.mCurrentTrackTitle = trackTitle;
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI成取消订阅
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI成订阅
            mSubBtn.setText(R.string.sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {

    }

    @Override
    public void onSubFull() {

        Toast.makeText(this, "订阅数量不得超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unregisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unregisterViewCallback(this);
        }
    }
}
