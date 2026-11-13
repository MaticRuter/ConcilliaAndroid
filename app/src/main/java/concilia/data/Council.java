package concilia.data;

import java.util.List;

public final class Council {
    public final int id;
    public final int number;
    public final String name;
    public final String year;
    public final String location;
    public final String era;
    public final int eraColor; // ARGB
    public final String heresy;
    public final String decision;
    public final String keyFigure;
    public final String keyFigureRole;
    public final String summary;
    public final String quote;
    public final String quoteSource;
    public final List<Lesson> lessons;

    public Council(int id, int number, String name, String year, String location,
                   String era, int eraColor, String heresy, String decision,
                   String keyFigure, String keyFigureRole, String summary,
                   String quote, String quoteSource, List<Lesson> lessons) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.year = year;
        this.location = location;
        this.era = era;
        this.eraColor = eraColor;
        this.heresy = heresy;
        this.decision = decision;
        this.keyFigure = keyFigure;
        this.keyFigureRole = keyFigureRole;
        this.summary = summary;
        this.quote = quote;
        this.quoteSource = quoteSource;
        this.lessons = lessons;
    }
}
