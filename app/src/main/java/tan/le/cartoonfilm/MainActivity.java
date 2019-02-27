package tan.le.cartoonfilm;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import tan.le.cartoonfilm.api.FilmHandler;
import tan.le.cartoonfilm.base.BaseActivity;
import tan.le.cartoonfilm.model.FilmModel;
import tan.le.cartoonfilm.model.FilmResponse;
import tan.le.cartoonfilm.utils.AppUtils;
import tan.le.cartoonfilm.utils.Constant;
import tan.le.cartoonfilm.utils.DebugLog;
import tan.le.cartoonfilm.utils.GridSpacingItemDecoration;
import tan.le.cartoonfilm.utils.SPRecyclerView;
import tan.le.cartoonfilm.view.adapter.FilmAdapter;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, SPRecyclerView.OnMoreListener {
    @BindView(R.id.rv_film)
    SPRecyclerView rvFilm;

    private FilmAdapter filmAdapter;

    private ArrayList<FilmModel> filmModels;
    private boolean isLoading = false;

    private int pageNo = 1;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(contextDagger, 2);
        rvFilm.setLayoutManager(layoutManager);
        rvFilm.addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(contextDagger, 5), true));
        rvFilm.setRefreshListener(this);
        rvFilm.setOnMoreListener(this);
        loadFilmList(false, false);
    }

    private void loadFilmList(boolean isLoadMore, boolean isRefresh) {
        if (!isLoadMore) {
            pageNo = 1;
            if (isRefresh)
                hideProgressDialog();
            else showProgressDialog();
        } else {
            pageNo++;
        }
        FilmHandler.loadFilms(Constant.API_KEY, Constant.KEY_LANGUAGE, pageNo)
                .subscribe(new Observer<FilmResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FilmResponse model) {
                        if (model != null) {
                            isLoading = true;
                            showFilmList(model.getResults());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                    }
                });
    }

    private void showFilmList(ArrayList<FilmModel> list) {
        DebugLog.d("===>" + list.get(0).getTitle());

        if (list != null && list.size() > 0) {
            if (pageNo == 1) {
                rvFilm.hideEmptyLayout();
                filmModels = new ArrayList<>(list);
                filmAdapter = new FilmAdapter(filmModels);
                rvFilm.setAdapter(filmAdapter);
                filmAdapter.notifyDataSetChanged();
            } else {
                filmAdapter.addMoreBook(list);
            }

        } else {
            if (pageNo > 1) {
                rvFilm.hideMoreProgress();
                return;
            }
            if (filmModels == null || filmModels.size() == 0)
                new ArrayList<>();
            else rvFilm.clear();
            rvFilm.showEmptyLayout();
        }

        if (isLoading)
            rvFilm.hideMoreProgress();
    }

    @Override
    public void onRefresh() {
        loadFilmList(false, true);
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        loadFilmList(true, false);
    }
}
