package scraper;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "showtime")
public class Showtime {
    @Id
    @GeneratedValue
    @Column(name = "showtime_id")
    private Long showtimeID;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar time;

    /*@ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;*/

    public Long getShowtimeID() {
        return showtimeID;
    }

    public void setShowtimeID(Long showtimeID) {
        this.showtimeID = showtimeID;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    /*public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }*/
}
