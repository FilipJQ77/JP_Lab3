/*
    Program: Aplikacja z graficznym interfejsem i operacjami na kolekcjach
    Plik: CollectionApp.java
    Autor: Filip Przygoński, 248892
    Data: Listopad 2019
*/

import javax.naming.InvalidNameException;
import java.io.*;
import java.util.ArrayList;

/**
 * klasa zajmująca się włączeniem aplikacji oraz zarządzaniem kolekcjami
 */
public class CollectionApp {

    /**
     * lista wszystkich kolekcji aplikacji
     */
    private static ArrayList<CollectionOfPosters> appCollections = new ArrayList<>();

    /**
     * tworzy nową kolekcję o danej nazwie i typie, oraz dodaję ją do listy kolekcji
     * @param collectionName nazwa kolekcji
     * @param collectionType typ kolekcji
     * @return zwraca stworzoną kolekcję
     * @throws InvalidNameException niepoprawna nazwa/typ kolekcji
     */
    static CollectionOfPosters newCollection(String collectionName, CollectionType collectionType) throws InvalidNameException {
        CollectionOfPosters newCollection = new CollectionOfPosters(collectionName, collectionType);
        appCollections.add(newCollection);
        return newCollection;
    }

    /**
     * dodaje daną kolekcję do listy kolekcji
     * @param collectionOfPosters kolekcja
     */
    static void addCollection(CollectionOfPosters collectionOfPosters) {
        appCollections.add(collectionOfPosters);
    }

    /**
     * zwraca kolekcję z listy kolekcji o danym indeksie
     * @param index jw
     * @return jw
     */
    static CollectionOfPosters getCollection(int index) {
        return appCollections.get(index);
    }

    /**
     * usuwa z listy kolekcji listę o danym indeksie
     * @param index
     */
    static void deleteCollection(int index) {
        CollectionOfPosters toRemove = appCollections.get(index);
        toRemove.clear();
        toRemove.collectionHasChanged();
        if (toRemove instanceof SpecialCollectionOfPosters) {
            ((SpecialCollectionOfPosters) toRemove).deleteParentCollectionsObservers();
        }
        appCollections.remove(index);
    }

    /**
     * wczytuje pojedynczą kolekcję
     * @param filename ścieżka pliku
     * @return wczytana kolekcja
     * @throws ClassNotFoundException
     * @throws PosterException
     * @throws InvalidNameException
     * @throws IOException
     */
    static CollectionOfPosters loadCollection(String filename) throws ClassNotFoundException, PosterException, InvalidNameException, IOException {
        CollectionOfPosters loadedCollection = CollectionOfPosters.loadCollectionFromFile(filename);
        appCollections.add(loadedCollection);
        return loadedCollection;
    }

    /**
     * wczytuje listę kolekcji, zapisaną wcześniej przez metodę saveTheListOfCollectionsToFile
     * @param filename ścieżka pliku
     * @return wczytana lista kolekcji
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static ArrayList<CollectionOfPosters> loadAListOfCollectionsFromFile(String filename) throws IOException, ClassNotFoundException {
        ArrayList<CollectionOfPosters> listOfCollections;
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
        try {
            listOfCollections = (ArrayList<CollectionOfPosters>) inputStream.readObject();
        } catch (ClassCastException e) {
            throw new IOException("Nie udalo sie wczytac listy kolekcji");
        }
        appCollections = listOfCollections;
        return appCollections;
    }

    /**
     * zapisuje wybraną kolekcje do pliku
     * @param filename ścieżka pliku
     * @param index indeks kolekcji
     * @throws IOException nie udało się znaleźć pliku
     */
    static void saveCollection(String filename, int index) throws IOException {
        appCollections.get(index).saveCollectionToFile(filename);
    }

    /**
     * zapisuje wszystkie zapisane kolekcje do jednego pliku
     * @param filename ścieżka pliku
     * @throws IOException
     */
    static void saveTheListOfCollectionsToFile(String filename) throws IOException {
        if (filename.endsWith(".bin")) {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
            outputStream.writeObject(appCollections);
        } else throw new IOException("Plik musi byc typu .bin");
    }

    public static ArrayList<CollectionOfPosters> getAppCollections() {
        return appCollections;
    }

    public static void main(String[] args) throws IOException, InvalidNameException, PosterException {
        new AppWindow();
    }

}

