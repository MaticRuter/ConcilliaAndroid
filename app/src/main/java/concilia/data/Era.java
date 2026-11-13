package concilia.data;

public final class Era {
    public final String name;
    public final int color;      // ARGB
    public final String range;
    public final int[] councils; // council numbers in this era

    public Era(String name, int color, String range, int[] councils) {
        this.name = name;
        this.color = color;
        this.range = range;
        this.councils = councils;
    }
}
