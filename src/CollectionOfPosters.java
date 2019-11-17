/*
    Program: Aplikacja z graficznym interfejsem i operacjami na kolekcjach
    Plik: CollectionOfPosters.java
    Autor: Filip Przygoński, 248892
    Data: Listopad 2019
*/

import javax.naming.InvalidNameException;
import java.io.*;
import java.util.*;

/**
 * klasa wyliczająca możliwe kolekcje plakatów
 */
enum CollectionType {

    VECTOR("Vector"),
    ARRAY_LIST("ArrayList"),
    LINKED_LIST("LinkedList"),
    HASH_SET("HashSet"),
    TREE_SET("TreeSet");

    String collectionTypeName;

    CollectionType(String type_name) {
        collectionTypeName = type_name;
    }

    @Override
    public String toString() {
        return collectionTypeName;
    }

    public static CollectionType findCollectionType(String searchedCollectionName) {
        for (CollectionType type : CollectionType.values()) {
            if (searchedCollectionName.equals(type.collectionTypeName))
                return type;
        }
        return null;
    }
}

public class CollectionOfPosters implements Iterable<Poster>, Serializable {

    private static final long serialVersionUID = 7410902562244000480L;
    private String collectionName;
    private CollectionType collectionType;
    private Collection<Poster> collection;

    public CollectionOfPosters() {
        //jak w klasie Poster, domyślny konstruktor ma zawsze poprawne wartości
        collectionName = "Nienazwana kolekcja";
        collectionType = CollectionType.VECTOR; //domyślna kolekcja to wektor
        try {
            setCollection(createCollection());
        } catch (InvalidNameException e) {
            System.out.println("Something has gone terribly wrong");
            //ten wyjątek nie powinien nigdy się wydarzyć
        }
    }

    public CollectionOfPosters(String collectionName, String collectionType) throws InvalidNameException {
        setCollectionName(collectionName);
        setCollectionType(collectionType);
        setCollection(createCollection());
    }

    public CollectionOfPosters(String collectionName, CollectionType collectionType) throws InvalidNameException {
        setCollectionName(collectionName);
        setCollectionType(collectionType);
        setCollection(createCollection());
    }

    public int size() {
        return collection.size();
    }

    public boolean add(Poster poster) {
        return collection.add(poster);
    }

    public boolean remove(Poster poster){
        return collection.remove(poster);
    }

    public void saveCollectionToFile(String filename) throws IOException {
        if (filename.endsWith(".txt")) {
            saveCollectionToTxt(filename);
        } else if (filename.endsWith(".bin")) {
            saveCollectionToBin(filename);
        } else throw new IOException("Niepoprawny typ pliku");
    }

    public static CollectionOfPosters loadCollectionFromFile(String filename) throws IOException, InvalidNameException, PosterException, ClassNotFoundException {
        if (filename.endsWith(".txt")) {
            return loadCollectionFromTxt(filename);
        } else if (filename.endsWith(".bin")) {
            return loadCollectionFromBin(filename);
        } else throw new IOException("Niepoprawny typ pliku");

    }

    public void saveCollectionToTxt(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename, false));
        writer.println(collectionName);
        writer.println(collectionType);
        for (Poster i : collection) {
            i.savePosterToTxtFile(writer);
        }
        writer.close();
    }

    public void saveCollectionToBin(String filename) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
        outputStream.writeObject(this);
        outputStream.close();
    }

    public static CollectionOfPosters loadCollectionFromTxt(String filename) throws FileNotFoundException, InvalidNameException, PosterException {
        Scanner scanner = new Scanner(new File(filename));
        String collectionName = scanner.nextLine();
        String collectionType = scanner.nextLine();
        CollectionOfPosters newCollectionOfPosters = new CollectionOfPosters(collectionName, collectionType);
        while (scanner.hasNextLine()) {
            newCollectionOfPosters.add(Poster.loadPosterFromTxtFile(scanner));
        }
        return newCollectionOfPosters;
    }

    public static CollectionOfPosters loadCollectionFromBin(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
        CollectionOfPosters newCollectionOfPosters = (CollectionOfPosters) inputStream.readObject();
        inputStream.close();
        return newCollectionOfPosters;
    }

    @Override
    public Iterator<Poster> iterator() {
        return collection.iterator();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(collectionName).append("\n");
        stringBuilder.append(collectionType).append("\n");
        for (Poster i : collection) {
            stringBuilder.append(i).append("\n");
        }
        return stringBuilder.toString();
    }

    private Collection<Poster> createCollection() throws InvalidNameException {
        switch (collectionType) {
            case VECTOR:
                return new Vector<>();
            case ARRAY_LIST:
                return new ArrayList<>();
            case LINKED_LIST:
                return new LinkedList<>();
            case TREE_SET:
                return new TreeSet<>();
            case HASH_SET:
                return new HashSet<>();
            default:
                throw new InvalidNameException("Dany typ kolekcji nie istnieje lub nie jest obslugiwany");
        }
    }

    public void changeCollectionType(String collectionType) throws InvalidNameException {
        changeCollectionType(CollectionType.findCollectionType(collectionType));
    }

    public void changeCollectionType(CollectionType collectionType) throws InvalidNameException {
        setCollectionType(collectionType);
        Collection<Poster> newCollection = createCollection();
        newCollection.addAll(collection);
        setCollection(newCollection);
    }

    public void changeCollection(CollectionOfPosters collectionOfPosters) throws InvalidNameException {
        setCollectionType(collectionOfPosters.collectionType);
        setCollection(collectionOfPosters.collection);
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) throws InvalidNameException {
        if (collectionName == null || collectionName.equals(""))
            throw new InvalidNameException("Niepoprawna nazwa kolekcji");
        else this.collectionName = collectionName;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    /**
     * metoda prywatna, ponieważ za zmianę typu istniejącej kolekcji jest odpowiedzialna biorąca wszystko pod uwagę metoda changeCollectionType
     *
     * @param collectionName
     * @throws InvalidNameException
     */
    private void setCollectionType(String collectionName) throws InvalidNameException {
        setCollectionType(CollectionType.findCollectionType(collectionName));
    }

    /**
     * metoda prywatna, ponieważ za zmianę typu istniejącej kolekcji jest odpowiedzialna biorąca wszystko pod uwagę metoda changeCollectionType
     *
     * @param collectionType
     * @throws InvalidNameException
     */
    private void setCollectionType(CollectionType collectionType) throws InvalidNameException {
        if (collectionType == null)
            throw new InvalidNameException("Dany typ kolekcji nie istnieje lub nie jest obslugiwany");
        this.collectionType = collectionType;
    }

    public Collection<Poster> getCollection() {
        return collection;
    }

    /**
     * metoda prywatna, aby zmienić kolekcję należy użyć uwzględniającej wszystko metody changeCollection
     *
     * @param collection
     */
    private void setCollection(Collection<Poster> collection) {
        this.collection = collection;
    }

}
