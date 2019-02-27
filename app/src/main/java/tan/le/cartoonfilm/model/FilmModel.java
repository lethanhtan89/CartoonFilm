package tan.le.cartoonfilm.model;

public class FilmModel {
    private int id;
    private boolean video;
    private float vote_average;
    private String title;
    private String poster_path;
    private String overview;

    public float getVoteAverage() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
