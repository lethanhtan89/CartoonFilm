package tan.le.cartoonfilm.api;

import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import tan.le.cartoonfilm.model.FilmResponse;

public class FilmHandler extends BaseApiHandler{
    public static Observable<FilmResponse> loadFilms(String key, String language, int page){
        Retrofit retrofit = buildAuthorizationRetrofit();
        ServerAPI api = retrofit.create(ServerAPI.class);
        return  api.loadFilmList(key, language, page).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
