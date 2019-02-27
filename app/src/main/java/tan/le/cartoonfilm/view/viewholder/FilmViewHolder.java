package tan.le.cartoonfilm.view.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tan.le.cartoonfilm.R;
import tan.le.cartoonfilm.model.FilmModel;
import tan.le.cartoonfilm.utils.ImageUtils;

public class FilmViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.img_film)
    ImageView imgFilm;
    @BindView(R.id.tv_average)
    TextView tvAverange;

    public FilmViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(FilmModel film) {
        tvAverange.setVisibility(View.VISIBLE);
        tvAverange.setText(film.getVoteAverage() + "");
        ImageUtils.loadImage(film.getPosterPath(), imgFilm, itemView.getContext());
    }
}
