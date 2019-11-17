/*
    Program: Aplikacja z graficznym interfejsem i operacjami na kolekcjach
    Plik: CollectionApp.java
    Autor: Filip Przygoński, 248892
    Data: Listopad 2019
*/

import javax.naming.InvalidNameException;
import java.io.*;
import java.util.ArrayList;

public class CollectionApp {

    private static ArrayList<CollectionOfPosters> appCollections = new ArrayList<>();

    static CollectionOfPosters newCollection(String collectionName, CollectionType collectionType) throws InvalidNameException {
        CollectionOfPosters newCollection = new CollectionOfPosters(collectionName, collectionType);
        appCollections.add(newCollection);
        return newCollection;
    }

    static void deleteCollection(int index) {
        appCollections.remove(index);
    }

    static CollectionOfPosters loadCollection(String filename) throws ClassNotFoundException, PosterException, InvalidNameException, IOException {
        CollectionOfPosters loadedCollection = CollectionOfPosters.loadCollectionFromFile(filename);
        appCollections.add(loadedCollection);
        return loadedCollection;
    }

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

    static void saveCollection(String filename, int index) throws IOException {
        appCollections.get(index).saveCollectionToFile(filename);
    }

    static void saveTheListOfCollectionsToFile(String filename) throws IOException {
        if (filename.endsWith(".bin")) {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
            outputStream.writeObject(appCollections);
        } else throw new IOException("Plik musi byc typu .bin");
    }

    public static ArrayList<CollectionOfPosters> getAppCollections() {
        return appCollections;
    }

    public static void main(String[] args) {
        //TODO I give up
        new AppWindow();
    }

}

