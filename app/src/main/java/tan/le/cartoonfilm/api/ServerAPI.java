package tan.le.cartoonfilm.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tan.le.cartoonfilm.model.FilmResponse;

public interface ServerAPI {
    @GET("movie/now_playing")
    Observable<FilmResponse> loadFilmList(@Query("api_key") String key, @Query("language") String language, @Query("page") int page);
}
