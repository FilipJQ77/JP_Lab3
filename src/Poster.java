/*
    Program: Aplikacja z graficznym interfejsem i operacjami na kolekcjach
    Plik: Poster.java
    Autor: Filip Przygo�ski, 248892
    Data: Listopad 2019
*/

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;

/**
 * Typ wyliczeniowy wyliczaj�cy mo�liwe tematy plakatu
 */
enum PosterTheme {

    NONE("Brak tematu"),
    SCHOOL("Szkola"),
    SCIENCE("Nauka"),
    MOVIE("Film"),
    MUSIC("Muzyka"),
    VIDEOGAME("Gra wideo");

    String posterTheme;

    PosterTheme(String theme) {
        posterTheme = theme;
    }

    @Override
    public String toString() {
        return posterTheme;
    }
}

/**
 * Klasa obs�uguj�ca wyj�tki dotycz�ce plakatu
 */
class PosterException extends Exception {
    PosterException(String message) {
        super(message);
    }
}

/**
 * Klasa reprezentuj�ca plakat
 */
public class Poster implements Serializable, Comparable {

    private static final long serialVersionUID = -5690503393120281768L;
    private int width; // w centymetrach
    private int height; // w centymetrach
    private String name;
    private PosterTheme theme;

    public Poster() {
        width = 1;
        height = 1;
        name = "Pusty plakat";
        theme = PosterTheme.NONE;
        //nie u�yte metody set aby domy�lny konstruktor Poster z zawsze poprawnymi warto�ciami nie musia� by� ci�gle robiony w bloku try-catch
    }

    public Poster(int width, int height) throws PosterException {
        setWidth(width);
        setHeight(height);
        setName("Pusty plakat");
        setTheme(PosterTheme.NONE);
    }

    public Poster(int width, int height, String name, String theme) throws PosterException {
        setWidth(width);
        setHeight(height);
        setName(name);
        setTheme(theme);
    }

    public Poster(int width, int height, String name, PosterTheme theme) throws PosterException {
        setWidth(width);
        setHeight(height);
        setName(name);
        setTheme(theme);
    }

    @Override
    public String toString() {
        return name + " " + "Temat: " + theme.posterTheme + " " + width + "cm x " + height + "cm";
    }

    @Override
    public boolean equals(Object obj) {
        Poster poster;
        try {
            poster = (Poster) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (!this.name.equals(poster.name))
            return false;

        else if (!this.theme.equals(poster.theme))
            return false;

        else if (!(this.width == poster.width))
            return false;

        else if (!(this.height == poster.height))
            return false;

        return true;
    }

    /**
     * sortuje 2 plakaty nast�puj�co:
     * 1. wed�ug nazwy, rosn�co alfabetycznie
     * 2. je�li maj� tak� sam� nazw�, wed�ug tematu plakatu, rosn�co alfabetycznie
     * 3. je�li maj� taki sam temat, wed�ug rozmiaru(pola powierzchni) plakatu, rosn�co
     * 4. je�li maj� takie samo pole, wed�ug szeroko�ci rosn�co
     * 5. je�li maj� takie same wymiary, plakaty b�d� sobie r�wne
     *
     * @param obj obiekt z kt�rym b�dzie por�wnywany plakat
     * @return liczba ujemna, gdy obiekt obj jest "wi�kszy" od this, 0 gdy s� sobie r�wne, liczba dodatnia gdy obiekt obj jest "mniejszy" od this
     */
    @Override
    public int compareTo(Object obj) {
        Poster poster;
        poster = (Poster) obj;
        int check;

        //nazwa
        check = this.name.compareToIgnoreCase(poster.name);
        if (check != 0) return check;

        //temat
        check = this.theme.toString().compareToIgnoreCase(poster.theme.toString());
        if (check != 0) return check;

        //rozmiar plakatu
        int thisSize = this.width * this.height;
        int posterSize = poster.width * poster.height;
        check = Integer.compare(thisSize, posterSize);
        if (check != 0) return check;

        //szeroko��
        return Integer.compare(this.width, poster.width);
    }

    @Override
    public int hashCode() {
        int result = 27;
        // mno�enie przez 31 poniewa� "A nice property of 31 is that the multiplication can be replaced by a shift and a subtraction for better performance" ~ Effective Java
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + name.hashCode();
        result = 31 * result + theme.hashCode();
        return result;
    }

    /**
     * zapisuje plakat do podanego pliku
     *
     * @param filename nazwa/�cie�ka pliku
     * @throws IOException gdy nie znaleziono pliku o podanej nazwie
     */
    public void savePosterToTxtFile(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename, true));
        savePosterToTxtFile(writer);
        writer.close();
    }

    /**
     * zapisuje plakat do pliku, ta metoda jest wykorzystywana w przypadku gdy chcemy zapisa� wiele plakat�w, �eby nie tworzy� dla ka�dego plakatu osobnego PrintWritera
     *
     * @param writer PrintWriter odpowiedzialny za zapis do pliku
     */
    public void savePosterToTxtFile(PrintWriter writer) {
        writer.println(width + "#" + height + "#" + name + "#" + theme.posterTheme);
    }

    /**
     * wczytuje dane z pliku i tworzy z nich plakat
     *
     * @param scanner scanner zewn�trzny
     * @return utworzony plakat
     * @throws PosterException gdy z danych nie da si� utworzy� plakatu
     */
    public static Poster loadPosterFromTxtFile(Scanner scanner) throws PosterException {
        String readData = scanner.nextLine();
        String[] data = readData.split("#");
        try {
            return new Poster(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], data[3]);
        } catch (NumberFormatException e) {
            throw new PosterException("Podana szeroko�� i/lub wysoko�� nie jest liczba");
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) throws PosterException {
        if (width <= 0) {
            throw new PosterException("Szeroko�� plakatu musi by� nieujemna.");
        } else this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) throws PosterException {
        if (height <= 0) {
            throw new PosterException("Wysoko�� plakatu musi by� nieujemna.");
        } else this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws PosterException {
        if (name == null || name.equals("")) {
            throw new PosterException("Nazwa plakatu niepoprawna.");
        } else this.name = name;
    }

    public PosterTheme getTheme() {
        return theme;
    }

    public void setTheme(PosterTheme theme) {
        this.theme = theme;
    }

    public void setTheme(String theme) throws PosterException {
        if (theme == null || theme.equals("") || theme.toUpperCase().equals("BRAK TEMATU")) {
            setTheme(PosterTheme.NONE);
            return;
        } else {
            for (PosterTheme i : PosterTheme.values()) {
                if (theme.toUpperCase().equals(i.posterTheme.toUpperCase())) {
                    setTheme(i);
                    return;
                }
            }
        }
        throw new PosterException("Niepoprawny temat plakatu.");
    }

}