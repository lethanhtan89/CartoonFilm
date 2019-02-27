package tan.le.cartoonfilm.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import tan.le.cartoonfilm.R;
import tan.le.cartoonfilm.model.FilmModel;
import tan.le.cartoonfilm.view.viewholder.FilmViewHolder;

public class FilmAdapter extends RecyclerView.Adapter<FilmViewHolder> {
    private ArrayList<FilmModel> filmModels;

    public FilmAdapter(ArrayList<FilmModel> filmModels) {
        this.filmModels = filmModels;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FilmViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_film, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int i) {
        holder.bind(filmModels.get(i));
    }

    @Override
    public int getItemCount() {
        return filmModels.size() == 0 ? 0 : filmModels.size();
    }

    public boolean isFilmExist(FilmModel filmModel) {
        for (FilmModel film : filmModels) {
            if (film.getId() == filmModel.getId())
                return true;
        }
        return false;
    }

    public void addMoreBook(ArrayList<FilmModel> films) {
        if (films != null) {
            for (int i = 0; i < films.size(); i++) {
                if (!isFilmExist(films.get(i)))
                    filmModels.add(films.get(i));
            }
            notifyItemRangeChanged(filmModels.size(), films.size());
        }
    }
}
