package tan.le.cartoonfilm.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import tan.le.cartoonfilm.R;


public class SPRecyclerView extends FrameLayout {
    protected int ITEM_LEFT_TO_LOAD_MORE = 7;
    protected RecyclerView mRecycler;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;
    protected SwipeRefreshLayout mPtrLayout;
    private ViewStub mProgress;
    private ViewStub mMoreProgress;
    private ViewStub mEmpty;
    private View mProgressView;
    private View mMoreProgressView;
    private View mEmptyView;
    private int mScrollbarStyle;
    private int mEmptyId;
    private int mMoreProgressId;
    private LAYOUT_MANAGER_TYPE layoutManagerType;
    private RecyclerView.OnScrollListener mInternalOnScrollListener;
    private RecyclerView.OnScrollListener mSwipeDismissScrollListener;
    private OnMoreListener mOnMoreListener;
    private boolean isLoadingMore;
    private boolean isRefresh;
    private int mSPRecyclerViewMainLayout;
    private int mProgressId;
    private int[] lastScrollPositions;

    public SPRecyclerView(Context context) {
        super(context);
        initView();
    }

    public SPRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public SPRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SPRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initView();
    }

    public SwipeRefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SPRecyclerView);
        mSPRecyclerViewMainLayout = a.getResourceId(R.styleable.SPRecyclerView_mainLayoutId, R.layout.layout_progress_recyclerview);
        mScrollbarStyle = a.getResourceId(R.styleable.SPRecyclerView_scrollbarStyle, -1);
        mEmptyId = a.getResourceId(R.styleable.SPRecyclerView_layout_empty, 0);
        mMoreProgressId = a.getResourceId(R.styleable.SPRecyclerView_layout_moreProgress, R.layout.layout_more_progress);
        mProgressId = a.getResourceId(R.styleable.SPRecyclerView_layout_progress, R.layout.layout_progress);
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(mSPRecyclerViewMainLayout, this);
        mPtrLayout = v.findViewById(R.id.ptr_layout);
        mPtrLayout.setEnabled(false);

        mProgress = v.findViewById(android.R.id.progress);
        mProgress.setLayoutResource(mProgressId);
        mProgressView = mProgress.inflate();

        mMoreProgress = v.findViewById(R.id.more_progress);
        mMoreProgress.setLayoutResource(mMoreProgressId);
        mMoreProgressView = mMoreProgress.inflate();
        mMoreProgress.setVisibility(View.GONE);

        mEmpty = v.findViewById(R.id.empty);
        mEmpty.setLayoutResource(mEmptyId);

        if (mEmptyId != 0) {
            mEmptyView = mEmpty.inflate();
            mEmptyView.setVisibility(View.GONE);
        }

        initRecyclerView(v);
    }

    private void initRecyclerView(View view) {
        View recyclerView = view.findViewById(android.R.id.list);
        if (recyclerView instanceof RecyclerView) {
            mRecycler = (RecyclerView) recyclerView;

        } else {
            throw new IllegalArgumentException("SPRecyclerView works with a RecyclerView!");
        }

        mInternalOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    processOnMore();
                }

                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);
                if (mSwipeDismissScrollListener != null)
                    mSwipeDismissScrollListener.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                if (mSwipeDismissScrollListener != null)
                    mSwipeDismissScrollListener.onScrollStateChanged(recyclerView, newState);
            }
        };
        mRecycler.addOnScrollListener(mInternalOnScrollListener);

        if (mScrollbarStyle != -1) {
            mRecycler.setScrollBarStyle(mScrollbarStyle);
        }
    }

    private void processOnMore() {
        RecyclerView.LayoutManager layoutManager = mRecycler.getLayoutManager();
        int lastInvisibleItemPosition = getLastVisibleItemPosition(layoutManager);
        int invisibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if ((totalItemCount <= (lastInvisibleItemPosition + ITEM_LEFT_TO_LOAD_MORE)) &&
                (lastInvisibleItemPosition + 1) % 20 == 0 && !isLoadingMore)
            /*if (((totalItemCount - lastInvisibleItemPosition) <= ITEM_LEFT_TO_LOAD_MORE) && !isLoadingMore)*/ {
            if (mOnMoreListener != null) {
                if (!mPtrLayout.isRefreshing()) {
                    isLoadingMore = true;
                    mMoreProgress.setVisibility(View.VISIBLE);
                    mPtrLayout.setEnabled(false);
                    mOnMoreListener.onMoreAsked(mRecycler.getAdapter().getItemCount(), ITEM_LEFT_TO_LOAD_MORE, lastInvisibleItemPosition);
                }
            }
        }
    }

    private int getLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        int lastVisibleItemPosition = -1;

        if (layoutManagerType == null) {
            if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }
        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                lastVisibleItemPosition = caseStaggeredGrid(layoutManager);
                break;
        }
        return lastVisibleItemPosition;
    }

    private int caseStaggeredGrid(RecyclerView.LayoutManager layoutManager) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        if (lastScrollPositions == null)
            lastScrollPositions = new int[staggeredGridLayoutManager.getSpanCount()];

        staggeredGridLayoutManager.findLastVisibleItemPositions(lastScrollPositions);
        return findMax(lastScrollPositions);
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private void setAdapterInternal(RecyclerView.Adapter adapter, boolean compatibleWithPrevious, boolean removeRecyclerExistingViews) {
        if (compatibleWithPrevious) {
            mRecycler.swapAdapter(adapter, removeRecyclerExistingViews);
        } else {
            mRecycler.setAdapter(adapter);
        }
        mProgress.setVisibility(GONE);
        mRecycler.setVisibility(VISIBLE);
        mPtrLayout.setRefreshing(false);
        if (null != adapter) {
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    update();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    super.onItemRangeChanged(positionStart, itemCount, payload);
                    update();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    update();
                }

                private void update() {
                    mProgress.setVisibility(View.GONE);
                    //mMoreProgress.setVisibility(View.GONE);
                    isLoadingMore = false;
                    mPtrLayout.setRefreshing(false);
                    if (mRecycler.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else if (mEmptyId != 0) {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    mPtrLayout.setEnabled(true);

                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    update();
                }
            });
        }
        if (mEmptyId != 0) {
            if (adapter != null) {
                mEmptyView.setVisibility(adapter.getItemCount() > 0
                        ? View.GONE
                        : View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showEmptyLayout() {
        mEmptyView.setVisibility(VISIBLE);
    }

    public void hideEmptyLayout() {
        mEmptyView.setVisibility(GONE);
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }

    public void swapAdapter(RecyclerView.Adapter adapter, boolean removeAndRecyclerExistingViews) {
        setAdapterInternal(adapter, true, removeAndRecyclerExistingViews);
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        mRecycler.setAdapter(null);
    }

    /**
     * Show the progressbar
     */
    public void showProgress() {
        hideRecycler();
        if (mEmptyId != 0) {
            mEmptyView.setVisibility(View.GONE);
        }
        mProgress.setVisibility(View.VISIBLE);
    }

    public void showProgressNotHideEmpty() {
        hideRecycler();
        mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the progressbar and show the recycler
     */
    public void showRecycler() {
        hideProgress();
        if (mRecycler.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
            mEmpty.setVisibility(View.VISIBLE);
        } else if (mEmptyId != 0) {
            mEmpty.setVisibility(View.GONE);
        }
        mRecycler.setVisibility(View.VISIBLE);
    }

    public void showMoreProgress() {
        mMoreProgress.setVisibility(View.VISIBLE);
    }

    public void hideMoreProgress() {
        mMoreProgress.setVisibility(View.GONE);
    }

    public void setRefreshing(boolean refreshing) {
        mPtrLayout.setRefreshing(refreshing);
    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     */
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
    }

    public boolean getRefesh() {
        return mPtrLayout.isRefreshing();
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     */
    public void setRefreshingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
        mPtrLayout.setColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        mPtrLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Hide the progressbar
     */
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Hide the recycler
     */
    public void hideRecycler() {
        mRecycler.setVisibility(View.GONE);
    }

    /**
     * Set the scroll listener for the recycler
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    /**
     * Add the onItemTouchListener for the recycler
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        setAdapterInternal(adapter, false, false);
    }

    /**
     * Sets the More listener
     *
     * @param max Number of items before loading more
     */
    public void setupMoreListener(OnMoreListener onMoreListener, int max) {
        mOnMoreListener = onMoreListener;
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public void setOnMoreListener(OnMoreListener onMoreListener) {
        mOnMoreListener = onMoreListener;
    }

    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    /**
     * Enable/Disable the More event
     */
    public void setLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    /**
     * Remove the moreListener
     */
    public void removeMoreListener() {
        mOnMoreListener = null;
    }


    public void setOnTouchListener(OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }

    /**
     * @return inflated progress view or null
     */
    public View getProgressView() {
        return mProgressView;
    }

    /**
     * @return inflated more progress view or null
     */
    public View getMoreProgressView() {
        return mMoreProgressView;
    }

    /**
     * @return inflated empty view or null
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * Animate a scroll by the given amount of pixels along either axis.
     *
     * @param dx Pixels to scroll horizontally
     * @param dy Pixels to scroll vertically
     */
    public void smoothScrollBy(int dx, int dy) {
        mRecycler.smoothScrollBy(dx, dy);
    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    public interface OnMoreListener {
        void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition);
    }
}
