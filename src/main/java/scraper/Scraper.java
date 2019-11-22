package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Scraper {
    private File input;
    private Document doc;
    private MovieRepository movieRepository;
    private ShowtimeRepository showtimeRepository;

    public Scraper(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) throws IOException, ParseException {
        input = readFile("");
        doc = Jsoup.parse(input, "UTF-8");

        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;

        Elements movieList = doc.select("div#tcmcol2");
        Elements tvBoxShow = movieList.select("div.tvboxshw");
        Elements tvBoxNoirAlley = movieList.select("div.tvbox_noiralley");
        Elements tvBoxRegular = movieList.select("div#tcmcol2 > p:not(.tvboxshw), div#tcmcol2 > p:not(.tvbox_noiralley)");

        fillDatabase(tvBoxShow, tvBoxNoirAlley, tvBoxRegular);
    }

    private File readFile(String filepath) {
        return new File(filepath);
    }

    private Document readWebpage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private Map<String, String> extractMovieData(String movieData) {
        Map<String, String> mappedData = new HashMap<>();

        //String title = movieData.substring(0, movieData.indexOf("(")-1);
        String year = movieData.substring(movieData.indexOf("(")+1,movieData.indexOf(")"));
        String synopsis = movieData.substring(movieData.indexOf(":")+2, movieData.indexOf("Dir.")-1);
        String director = movieData.substring(movieData.indexOf("Dir.")+5);

        //mappedData.put("title", title);
        mappedData.put("year", year);
        mappedData.put("synopsis", synopsis);
        mappedData.put("director", director);

        return mappedData;
    }

    private Showtime extractShowtimeData(String showtimeData) throws ParseException {
        showtimeData = showtimeData.substring(showtimeData.indexOf(",")+2);

        String[] dateArray = showtimeData.split(" ");
        String monthName = dateArray[0];
        Date date = new SimpleDateFormat("MMMM").parse(monthName);
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        int monthNumber = Integer.parseInt(sdf.format(date))-1;
        int day = Integer.parseInt(dateArray[1].substring(0, dateArray[1].indexOf(",")));
        int hour = Integer.parseInt(dateArray[2].substring(0,dateArray[2].indexOf(":"))) + 3; // Add 3 for eastern time
        int minute = Integer.parseInt(dateArray[2].substring(dateArray[2].indexOf(":")+1));
        String amPm = dateArray[3];
        if(amPm.equals("PM")) {
            hour += 12;
        }

        Calendar c = new GregorianCalendar(2019, monthNumber, day, hour, minute);
        Showtime s = new Showtime();
        s.setTime(c);

        return s;

    }

    private void extractTvBoxRegularData(Elements tvBoxRegular) throws ParseException {

        for (int i = 0; i < tvBoxRegular.size(); i+=2) {
            Movie movie = new Movie();

            String showtime = tvBoxRegular.get(i).ownText();
            String title = tvBoxRegular.get(i+1).select("p > span").text();
            String everythingElse = tvBoxRegular.get(i+1).ownText();
            Map<String, String> extractedData = extractMovieData(everythingElse);

            movie.setTitle(title);
            movie.setYearReleased(extractedData.get("year"));
            movie.setSynopsis(extractedData.get("synopsis"));
            movie.setDirector(extractedData.get("director"));
            movie.setMovieType("Regular");

            Showtime st = extractShowtimeData(showtime);
            movie.addShowtimes(st);

            //System.out.println(title);
            //System.out.println(everythingElse);
            //System.out.println();

            showtimeRepository.save(st);
            movieRepository.save(movie);
        }
    }

    private void extractTvBoxNoirAlleyData(Elements tvBoxNoirAlley) throws ParseException {

        for (int i = 0; i < tvBoxNoirAlley.size(); i++) {

            Movie movie = new Movie();

            String[] noirAlleyShowtimes = tvBoxNoirAlley.get(i).select("p.date").text().split(" & ");
            String title = tvBoxNoirAlley.get(i).select("div > p").get(2).select("p > span").text();
            String noirAlleyEverythingElse = tvBoxNoirAlley.get(i).select("div > p").get(2).ownText();
            Map<String, String> extractedData = extractMovieData(noirAlleyEverythingElse);

            movie.setTitle(title);
            movie.setYearReleased(extractedData.get("year"));
            movie.setSynopsis(extractedData.get("synopsis"));
            movie.setDirector(extractedData.get("director"));
            movie.setMovieType("Noir Alley");
            for(String showtime : noirAlleyShowtimes) {
                Showtime st = extractShowtimeData(showtime);
                movie.addShowtimes(st);

                showtimeRepository.save(st);
            }

            movieRepository.save(movie);
        }
    }

    private void extractTvBoxShowData(Elements tvBoxShow) throws ParseException {

        for (int i = 0; i < tvBoxShow.size(); i++) {

            Elements showtimes = tvBoxShow.get(i).select("p.date");
            Elements movies = tvBoxShow.get(i).select("p:has(span)");
            String movieType = tvBoxShow.get(i).select("h2").text();

            for (int j = 0; j < movies.size(); j++) {
                Movie movie = new Movie();
                String title = movies.get(j).select("p > span").text();
                String everythingElse = movies.get(j).ownText();

                Map<String, String> extractedData = extractMovieData(everythingElse);

                movie.setTitle(title);
                movie.setYearReleased(extractedData.get("year"));
                movie.setSynopsis(extractedData.get("synopsis"));
                movie.setDirector(extractedData.get("director"));
                movie.setMovieType(movieType);

                String[] showtimePrefixArr = showtimes.get(0).text().split(", ");
                String showtimePrefix = showtimePrefixArr[0] + ", " + showtimePrefixArr[1] + ", ";

                Showtime st = extractShowtimeData(showtimePrefix + showtimes.get(j+1).text());
                movie.addShowtimes(st);

                showtimeRepository.save(st);
                movieRepository.save(movie);
            }
        }
    }

    public void fillDatabase(Elements tvBoxShow, Elements tvBoxNoirAlley, Elements tvBoxRegular) throws ParseException {

        extractTvBoxShowData(tvBoxShow);
        extractTvBoxNoirAlleyData(tvBoxNoirAlley);
        extractTvBoxRegularData(tvBoxRegular);

    }

}
