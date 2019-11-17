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

public class CollectionOfPosters extends Observable implements Iterable<Poster>, Serializable {

    protected static final long serialVersionUID = 544818347771453435L;
    protected String collectionName;
    protected CollectionType collectionType;
    protected Collection<Poster> collection;

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

    public boolean remove(Poster poster) {
        return collection.remove(poster);
    }

    public void clear() {
        collection.clear();
    }

    @Override
    public Iterator<Poster> iterator() {
        return collection.iterator();
    }

    void sortAlphabetically() {
        Collections.sort((List<Poster>) collection);
    }

    void sortByTheme() {
        //wyrażenie lambda
        Comparator newComparator = (o1, o2) -> {
            Poster p1 = (Poster) o1;
            Poster p2 = (Poster) o2;
            int comparison1 = p1.getTheme().compareTo(p2.getTheme());
            if (comparison1 != 0) return comparison1;
            int comparison2 = p1.getName().compareTo(p2.getName());
            if (comparison2 != 0) return comparison2;
            int comparison3 = Integer.compare(p1.getWidth() * p1.getHeight(), p2.getWidth() * p2.getHeight());
            if (comparison3 != 0) return comparison3;
            return Integer.compare(p1.getWidth(), p2.getWidth());
        };
        Collections.sort((List<Poster>) collection, newComparator);
    }

    void sortByArea() {
        //wyrażenie lambda
        Comparator newComparator = (o1, o2) -> {
            Poster p1 = (Poster) o1;
            Poster p2 = (Poster) o2;
            int comparison1 = Integer.compare(p1.getWidth() * p1.getHeight(), p2.getWidth() * p2.getHeight());
            if (comparison1 != 0) return comparison1;
            int comparison2 = Integer.compare(p1.getWidth(), p2.getWidth());
            if (comparison2 != 0) return comparison2;
            int comparison3 = p1.getName().compareTo(p2.getName());
            if (comparison3 != 0) return comparison3;
            return p1.getTheme().compareTo(p2.getTheme());
        };
        Collections.sort((List<Poster>) collection, newComparator);
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
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(collectionName).append(" ");
        stringBuilder.append(collectionType).append(" ");
        stringBuilder.append(" Ilosc elementow: ").append(this.size()).append("\n");
//        for (Poster i : collection) {
//            stringBuilder.append(i).append("\n");
//        }
        return stringBuilder.toString();
    }

    void collectionHasChanged() {
        super.setChanged();
        super.notifyObservers();
    }

    protected Collection<Poster> createCollection() throws InvalidNameException {
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
     * @param collectionType
     * @throws InvalidNameException
     */
    protected void setCollectionType(String collectionType) throws InvalidNameException {
        setCollectionType(CollectionType.findCollectionType(collectionType));
    }

    /**
     * metoda prywatna, ponieważ za zmianę typu istniejącej kolekcji jest odpowiedzialna biorąca wszystko pod uwagę metoda changeCollectionType
     *
     * @param collectionType
     * @throws InvalidNameException
     */
    protected void setCollectionType(CollectionType collectionType) throws InvalidNameException {
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
    protected void setCollection(Collection<Poster> collection) {
        this.collection = collection;
    }
}

class SpecialCollectionOfPosters extends CollectionOfPosters implements Observer {

    public final static byte UNION = 0;
    public final static byte INTERSECTION = 1;
    public final static byte DIFFERENCE = 2;
    public final static byte SYMMETRIC_DIFFERENCE = 3;

    private CollectionOfPosters firstCollection;
    private CollectionOfPosters secondCollection;
    private byte specialCollectionType;

    public SpecialCollectionOfPosters(CollectionOfPosters firstCollection, CollectionOfPosters secondCollection, byte specialCollectionType) throws Exception {
        super();
        setParentCollections(firstCollection, secondCollection);
        this.specialCollectionType = specialCollectionType;
        createSpecialCollection();
    }

    private void createSpecialCollection() throws Exception {
        this.collection.clear();
        if (specialCollectionType == UNION) {
            createUnionCollection();
        } else if (specialCollectionType == INTERSECTION) {
            createIntersectionCollection();
        } else if (specialCollectionType == DIFFERENCE) {
            createDifferenceCollection();
        } else if (specialCollectionType == SYMMETRIC_DIFFERENCE) {
            createSymmetricDifferenceCollection();
        }
    }

    private CollectionOfPosters makeCollectionCopy(CollectionOfPosters collection) throws InvalidNameException {
        CollectionOfPosters collectionCopy = new CollectionOfPosters(collection.collectionName, collection.collectionType);
        for(Poster poster : collection){
            collectionCopy.add(poster);
        }
        return collectionCopy;
    }

    private void createUnionCollection() throws InvalidNameException {
        CollectionOfPosters firstCollectionCopy = makeCollectionCopy(firstCollection);
        CollectionOfPosters secondCollectionCopy = makeCollectionCopy(secondCollection);
        this.setCollectionName(firstCollectionCopy.collectionName + " OR " + secondCollectionCopy.collectionName);
        this.setCollectionType(firstCollectionCopy.collectionType);
        addCollections(firstCollectionCopy, secondCollectionCopy);
    }

    private void createIntersectionCollection() throws Exception {
        CollectionOfPosters firstCollectionCopy = makeCollectionCopy(firstCollection);
        CollectionOfPosters secondCollectionCopy = makeCollectionCopy(secondCollection);
        this.setCollectionName(firstCollectionCopy.collectionName + " AND " + secondCollectionCopy.collectionName);
        this.setCollectionType(firstCollectionCopy.collectionType);
        Iterator iterator1 = firstCollectionCopy.iterator();
        while (iterator1.hasNext()) {
            Poster poster1 = (Poster) iterator1.next();
            Iterator iterator2 = secondCollectionCopy.iterator();
            while (iterator2.hasNext()) {
                Poster poster2 = (Poster) iterator2.next();
                if (poster1.equals(poster2)) {
                    this.add(poster1);
                    iterator1.remove();
                    iterator2.remove();
                    break;
                }
            }
        }
    }

    private void createDifferenceCollection() throws Exception {
        CollectionOfPosters firstCollectionCopy = makeCollectionCopy(firstCollection);
        CollectionOfPosters secondCollectionCopy = makeCollectionCopy(secondCollection);
        this.setCollectionName(firstCollectionCopy.collectionName + " \\ " + secondCollectionCopy.collectionName);
        this.setCollectionType(firstCollectionCopy.collectionType);
        removeIntersectionFromBothCollections(firstCollectionCopy, secondCollectionCopy);
        for (Poster poster : firstCollectionCopy) {
            this.add(poster);
        }
    }

    private void createSymmetricDifferenceCollection() throws Exception {
        CollectionOfPosters firstCollectionCopy = makeCollectionCopy(firstCollection);
        CollectionOfPosters secondCollectionCopy = makeCollectionCopy(secondCollection);
        this.setCollectionName(firstCollectionCopy.collectionName + " XOR " + secondCollectionCopy.collectionName);
        this.setCollectionType(firstCollectionCopy.collectionType);
        removeIntersectionFromBothCollections(firstCollectionCopy, secondCollectionCopy);
        addCollections(firstCollectionCopy, secondCollectionCopy);
    }

    private void addCollections(CollectionOfPosters firstCollectionCopy, CollectionOfPosters secondCollectionCopy) {
        for (Poster poster : firstCollectionCopy) {
            this.add(poster);
        }
        for (Poster poster : secondCollectionCopy) {
            this.add(poster);
        }
    }

    private void removeIntersectionFromBothCollections(CollectionOfPosters firstCollectionCopy, CollectionOfPosters secondCollectionCopy) {
        Iterator iterator1 = firstCollectionCopy.iterator();
        while (iterator1.hasNext()) {
            Poster poster1 = (Poster) iterator1.next();
            Iterator iterator2 = secondCollectionCopy.iterator();
            while (iterator2.hasNext()) {
                Poster poster2 = (Poster) iterator2.next();
                if (poster1.equals(poster2)) {
                    iterator1.remove();
                    iterator2.remove();
                    break;
                }
            }
        }
    }

    private void setParentCollections(CollectionOfPosters firstCollection, CollectionOfPosters secondCollection) throws Exception {
        if (!firstCollection.collectionType.equals(secondCollection.collectionType)) {
            throw new Exception("Kolekcje musza miec zgodne typy");
        }
        this.firstCollection = firstCollection;
        this.secondCollection = secondCollection;
        firstCollection.addObserver(this);
        secondCollection.addObserver(this);
    }

    void deleteParentCollectionsObservers() {
        firstCollection.deleteObserver(this);
        secondCollection.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            this.createSpecialCollection();
        } catch (Exception ignored) {}
    }
}
