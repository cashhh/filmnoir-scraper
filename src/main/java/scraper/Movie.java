package scraper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue
    @Column(name = "movie_id")
    private Long movieId;

    @OneToMany
    @JoinColumn(name = "movie_id")
    private List<Showtime> showtimes = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(name = "year_released", nullable = false, length = 4)
    private String yearReleased;

    @Column(name = "movie_type", nullable = false)
    private String movieType;

    @Column(nullable = false)
    private String director;

    @Lob
    @Column(nullable = false)
    private String synopsis;

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public List<Showtime> getShowtimes() {
        return showtimes;
    }

    public void addShowtimes(Showtime showtime) {
        showtimes.add(showtime);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYearReleased() {
        return yearReleased;
    }

    public void setYearReleased(String yearReleased) {
        this.yearReleased = yearReleased;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public String toString() {
        return String.format("[Movie ID: %s\nTitle: %s\nYear Released: %s\nMovie Type: %s\nDirector: %s\nSynopsis: %s]\n", movieId, title, yearReleased, movieType, director, synopsis);
    }
}
