/*
    Program: Aplikacja z graficznym interfejsem i operacjami na kolekcjach
    Plik: AppWindow.java
    Autor: Filip Przygoński, 248892
    Data: Listopad 2019
*/

import javax.naming.InvalidNameException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.*;

class PosterWindow extends JDialog implements ActionListener {

    private Poster poster;

    Font font = new Font("MonoSpaced", Font.BOLD, 12);

    JLabel widthLabel = new JLabel("Szerokosc plakatu: ");
    JLabel heightLabel = new JLabel("Wysokosc plakatu: ");
    JLabel nameLabel = new JLabel("Nazwa plakatu: ");
    JLabel themeLabel = new JLabel("Temat plakatu: ");

    JTextField widthTextField = new JTextField(10);
    JTextField heightTextField = new JTextField(10);
    JTextField nameTextField = new JTextField(10);
    JComboBox<PosterTheme> themeBox = new JComboBox<>(PosterTheme.values());

    JButton cancelButton = new JButton("Anuluj");
    JButton okButton = new JButton("OK");

    PosterWindow(CollectionWindow owner, Poster poster) {
        super(owner, ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setTitle("Tworzenie/Edycja plakatu");
        setLocationRelativeTo(owner);

        this.poster = poster;
        if (this.poster != null)
            showPoster(poster);

        widthLabel.setFont(font);
        heightLabel.setFont(font);
        nameLabel.setFont(font);
        themeLabel.setFont(font);

        cancelButton.addActionListener(this);
        okButton.addActionListener(this);

        JPanel panel = new JPanel();

        panel.add(widthLabel);
        panel.add(widthTextField);
        panel.add(heightLabel);
        panel.add(heightTextField);
        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(themeLabel);
        panel.add(themeBox);

        panel.add(cancelButton);
        panel.add(okButton);

        setContentPane(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object sourceOfEvent = e.getSource();
        if (sourceOfEvent == cancelButton) {
            clickedCancel();
        } else if (sourceOfEvent == okButton) {
            clickedOK();
        }
    }

    void showPoster(Poster poster) {
        widthTextField.setText(String.valueOf(poster.getWidth()));
        heightTextField.setText(String.valueOf(poster.getHeight()));
        nameTextField.setText(poster.getName());
        themeBox.setSelectedItem(poster.getTheme());
    }

    void clickedCancel() {
        if (JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz anulowac akcje?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.poster = null;
            dispose();
        }
    }

    void clickedOK() {
        boolean isError = false;
        int width = 0;
        int height = 0;
        try {
            width = Integer.parseInt(widthTextField.getText());
            height = Integer.parseInt(heightTextField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Podana szerokosc/wysokosc nie jest liczba", "Blad", JOptionPane.ERROR_MESSAGE);
            isError = true;
        }
        String name = nameTextField.getText();
        PosterTheme theme = (PosterTheme) themeBox.getSelectedItem();
        try {
            poster = new Poster(width, height, name, theme);
        } catch (PosterException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
            isError = true;
        }
        if (!isError) {
            dispose();
        }
    }

    public Poster getPoster() {
        return poster;
    }
}

class CollectionWindow extends JDialog implements ActionListener, IWindow {

    private CollectionOfPosters collectionOfPosters;

    private JMenuBar menuBar = new JMenuBar();
    private JMenu menuOptions = new JMenu("Plakat");
    private JMenu menuSort = new JMenu("Sortowanie");
    private JMenu menuCollection = new JMenu("Kolekcja");
    private JMenu menuProgram = new JMenu("Program");
    private JMenuItem menuNew = new JMenuItem("Stworz");
    private JMenuItem menuEdit = new JMenuItem("Edytuj");
    private JMenuItem menuSave = new JMenuItem("Zapisz");
    private JMenuItem menuSaveAll = new JMenuItem("Zapisz wszystkie");
    private JMenuItem menuLoad = new JMenuItem("Wczytaj");
    private JMenuItem menuDelete = new JMenuItem("Usun");
    private JMenuItem menuSortAlphabetically = new JMenuItem("Sortuj alfabetycznie");
    private JMenuItem menuSortByTheme = new JMenuItem("Sortuj po temacie");
    private JMenuItem menuSortByArea = new JMenuItem("Sortuj po powierzchni");
    private JMenuItem menuChangeCollectionName = new JMenuItem("Zmien nazwe kolekcji");
    private JMenuItem menuChangeCollectionType = new JMenuItem("Zmien typ kolekcji");
    private JMenuItem menuInfo = new JMenuItem("Info");

    private JPanel mainPanel = new JPanel();

    String[] tableHeader = {"Nazwa", "Temat", "Szerokosc", "Wysokosc"};
    DefaultTableModel tableModel = new DefaultTableModel(tableHeader, 0);
    JTable table = new JTable(tableModel) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; //aby tabela była nieedytowalna
        }
    };
    JScrollPane scrollPane = new JScrollPane(table);

    private JLabel collectionNameLabel = new JLabel("Nazwa kolekcji: ");
    private JLabel collectionTypeLabel = new JLabel("  Typ kolekcji: ");

    private JTextField collectionNameField = new JTextField(10);
    private JTextField collectionTypeField = new JTextField(10);

    private JButton newButton = new JButton("Nowy plakat");
    private JButton editButton = new JButton("Edytuj plakat");
    private JButton loadButton = new JButton("Wczytaj plakaty z pliku");
    private JButton saveButton = new JButton("Zapisz plakat do pliku");
    private JButton saveAllButton = new JButton("Zapisz wszystkie plakaty do pliku");
    private JButton deleteButton = new JButton("Usun aktualny plakat");

    CollectionWindow(AppWindow parent, CollectionOfPosters collection) {
        super(parent, ModalityType.DOCUMENT_MODAL);

        collectionOfPosters = collection;

        setSize(800, 600);
        setTitle("Menedzer kolekcji plakatow");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(parent);

        setJMenuBar(menuBar);
        menuBar.add(menuOptions);
        menuBar.add(menuSort);
        menuBar.add(menuCollection);
        menuBar.add(menuProgram);

        menuNew.addActionListener(this);
        menuEdit.addActionListener(this);
        menuSave.addActionListener(this);
        menuSaveAll.addActionListener(this);
        menuLoad.addActionListener(this);
        menuDelete.addActionListener(this);
        menuSortAlphabetically.addActionListener(this);
        menuSortByTheme.addActionListener(this);
        menuSortByArea.addActionListener(this);
        menuChangeCollectionName.addActionListener(this);
        menuChangeCollectionType.addActionListener(this);
        menuInfo.addActionListener(this);

        newButton.addActionListener(this);
        editButton.addActionListener(this);
        loadButton.addActionListener(this);
        saveButton.addActionListener(this);
        saveAllButton.addActionListener(this);
        deleteButton.addActionListener(this);

        menuOptions.add(menuNew);
        menuOptions.add(menuEdit);
        menuOptions.add(menuSave);
        menuOptions.add(menuSaveAll);
        menuOptions.add(menuLoad);
        menuOptions.add(menuDelete);
        menuSort.add(menuSortAlphabetically);
        menuSort.add(menuSortByTheme);
        menuSort.add(menuSortByArea);
        menuCollection.add(menuChangeCollectionName);
        menuCollection.add(menuChangeCollectionType);
        menuProgram.add(menuInfo);

        scrollPane.setPreferredSize(new Dimension(this.getWidth() - 50, this.getHeight() - 160));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Zbior plakatow"));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);

        //dodanie do tabeli kolekcji
        addCollectionToTable(collectionOfPosters);

        mainPanel.add(collectionNameLabel);
        mainPanel.add(collectionNameField);
        mainPanel.add(collectionTypeLabel);
        mainPanel.add(collectionTypeField);

        collectionNameField.setText(collectionOfPosters.getCollectionName());
        collectionTypeField.setText(String.valueOf(collectionOfPosters.getCollectionType()));

        mainPanel.add(scrollPane);

        collectionNameField.setEditable(false);
        collectionTypeField.setEditable(false);

        mainPanel.add(newButton);
        mainPanel.add(editButton);
        mainPanel.add(saveButton);
        mainPanel.add(saveAllButton);
        mainPanel.add(loadButton);
        mainPanel.add(deleteButton);

        setContentPane(mainPanel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object sourceOfEvent = e.getSource();
        if (sourceOfEvent == newButton || sourceOfEvent == menuNew) {
            newPoster();
        } else if (sourceOfEvent == editButton || sourceOfEvent == menuEdit) {
            editPoster();
        } else if (sourceOfEvent == loadButton || sourceOfEvent == menuLoad) {
            loadPosters();
        } else if (sourceOfEvent == saveButton || sourceOfEvent == menuSave) {
            savePoster();
        } else if (sourceOfEvent == saveAllButton || sourceOfEvent == menuSaveAll) {
            saveAllPosters();
        } else if (sourceOfEvent == deleteButton || sourceOfEvent == menuDelete) {
            deletePoster();
        } else if (sourceOfEvent == menuSortAlphabetically) {
            sortAlphabetically();
        } else if (sourceOfEvent == menuSortByTheme) {
            sortByTheme();
        } else if (sourceOfEvent == menuSortByArea) {
            sortByArea();
        } else if (sourceOfEvent == menuChangeCollectionName) {
            changeCollectionName();
        } else if (sourceOfEvent == menuChangeCollectionType) {
            changeCollectionType();
        } else if (sourceOfEvent == menuInfo) {
            info();
        }
    }

    void newPoster() {
        Poster poster = null;
        PosterWindow window = new PosterWindow(this, poster);
        poster = window.getPoster();
        if (poster != null) {
            collectionOfPosters.add(poster);
//            gdyby nie zbiory, można by zrobić po prostu:
//            addPosterToTable(poster);
            refreshTable();
        }
    }

    void editPoster() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            Poster posterBefore = getSelectedPoster(index);
            PosterWindow window = new PosterWindow(this, posterBefore);
            Poster posterAfter = window.getPoster();
            if (posterAfter != null) {
                collectionOfPosters.remove(posterBefore);
                collectionOfPosters.add(posterAfter);
//                jak wyżej, gdyby nie zbiory wystarczyłoby:
//                editPosterInTable(index, posterAfter);
                refreshTable();
            }
        }
    }

    void loadPosters() {
        String filename = chosenFile();
        if (filename == null) {
            return;
        }
        if (filename.endsWith(".txt")) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(filename));
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
            }
            if (scanner != null) {
                while (scanner.hasNextLine()) {
                    try {
                        Poster posterToAdd = Poster.loadPosterFromTxtFile(scanner);
                        collectionOfPosters.add(posterToAdd);
//                    addPosterToTable(posterToAdd);
                    } catch (PosterException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else if(filename.endsWith(".bin")){

        }

        refreshTable();
    }

    void savePoster() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            String filename = chosenFile();
            if (filename == null) {
                return;
            }
            Poster poster = getSelectedPoster(index);
            try {
                poster.savePosterToTxtFile(filename);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    void saveAllPosters() {
        String filename = chosenFile();
        if (filename == null) {
            return;
        }
        try {
            for (Poster i : collectionOfPosters) {
                i.savePosterToTxtFile(filename);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
        }
    }

    void deletePoster() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            Poster posterToDelete = getSelectedPoster(index);
            collectionOfPosters.remove(posterToDelete);
            tableModel.removeRow(index);
        }
    }

    void sortAlphabetically() {
        if (isCollectionASet()) {
            JOptionPane.showMessageDialog(this, "Kolekcja typu zbior nie moze byc sortowana", "Blad", JOptionPane.ERROR_MESSAGE);
        } else {
            Collections.sort((List<Poster>) collectionOfPosters.getCollection());
            refreshTable();
        }
    }

    void sortByTheme() {
        if (isCollectionASet()) {
            JOptionPane.showMessageDialog(this, "Kolekcja typu zbior nie moze byc sortowana", "Blad", JOptionPane.ERROR_MESSAGE);
        } else {
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
            Collections.sort((List<Poster>) collectionOfPosters.getCollection(), newComparator);
            refreshTable();
        }
    }

    void sortByArea() {
        if (isCollectionASet()) {
            JOptionPane.showMessageDialog(this, "Kolekcja typu zbior nie moze byc sortowana", "Blad", JOptionPane.ERROR_MESSAGE);
        } else {
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
            Collections.sort((List<Poster>) collectionOfPosters.getCollection(), newComparator);
            refreshTable();
        }
    }

    void changeCollectionName() {
        String newName = String.valueOf(JOptionPane.showInputDialog(this, "Podaj nowa nazwe kolekcji", "", JOptionPane.INFORMATION_MESSAGE, null, null, collectionOfPosters.getCollectionName()));
        try {
            collectionOfPosters.setCollectionName(newName);
            collectionNameField.setText(newName);
        } catch (InvalidNameException e) {
            JOptionPane.showInputDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
        }
    }

    void changeCollectionType() {
        CollectionType newType = (CollectionType) JOptionPane.showInputDialog(this, "Wybierz nowy typ kolekcji", "", JOptionPane.INFORMATION_MESSAGE, null, CollectionType.values(), collectionOfPosters.getCollectionType());
        try {
            collectionOfPosters.changeCollectionType(newType);
            collectionTypeField.setText(String.valueOf(newType));
            refreshTable();
        } catch (InvalidNameException e) {
            JOptionPane.showInputDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
        }
    }

    boolean isCollectionASet() {
        CollectionType collectionType = collectionOfPosters.getCollectionType();
        return collectionType == CollectionType.HASH_SET || collectionType == CollectionType.TREE_SET;
    }

    Poster getSelectedPoster(int index) {
        int tempIndex = index;
        Iterator iterator = collectionOfPosters.iterator();
        while (tempIndex > 0) {
            --tempIndex;
            iterator.next();
        }
        return (Poster) iterator.next();
    }

    void addPosterToTable(Poster poster) {
        String[] data = {poster.getName(), String.valueOf(poster.getTheme()), String.valueOf(poster.getWidth()), String.valueOf(poster.getHeight())};
        tableModel.addRow(data);
    }

    void addCollectionToTable(CollectionOfPosters collection) {
        for (Poster poster : collection) {
            addPosterToTable(poster);
        }
    }

    void editPosterInTable(int index, Poster editedPoster) {
        tableModel.setValueAt(editedPoster.getName(), index, 0);
        tableModel.setValueAt(editedPoster.getTheme(), index, 1);
        tableModel.setValueAt(editedPoster.getWidth(), index, 2);
        tableModel.setValueAt(editedPoster.getHeight(), index, 3);
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        for (Poster poster : collectionOfPosters) {
            addPosterToTable(poster);
        }
    }
}

public class AppWindow extends JFrame implements ActionListener, IWindow {

    //Pasek menu
    JMenuBar menuBar = new JMenuBar();
    JMenu menuCollection = new JMenu("Kolekcja");
    JMenu menuSpecialCollection = new JMenu("Specjalna Kolekcja");
    JMenu menuProgram = new JMenu("Program");
    JMenuItem menuNew = new JMenuItem("Nowa kolekcja");
    JMenuItem menuEdit = new JMenuItem("Edytuj kolekcje");
    JMenuItem menuDelete = new JMenuItem("Usun kolekcje");
    JMenuItem menuLoad = new JMenuItem("Wczytaj kolekcje");
    JMenuItem menuLoadAll = new JMenuItem("Wczytaj liste kolekcji");
    JMenuItem menuSave = new JMenuItem("Zapisz wybrana kolekcje");
    JMenuItem menuSaveAll = new JMenuItem("Zapisz wszystkie kolekcje");
    JMenuItem menuUnion = new JMenuItem("Suma kolekcji");
    JMenuItem menuIntersection = new JMenuItem("Iloczyn kolekcji");
    JMenuItem menuDifference = new JMenuItem("Roznica kolekcji");
    JMenuItem menuSymmetricDifference = new JMenuItem("Roznica symetryczna kolekcji");
    JMenuItem menuInfo = new JMenuItem("Info");
    JMenuItem menuExit = new JMenuItem("Wyjdz");

    //Okno aplikacji
    JPanel mainPanel = new JPanel();
    String[] tableHeader = {"Nazwa kolekcji", "Typ kolekcji", "Ilosc plakatow"};
    DefaultTableModel tableModel = new DefaultTableModel(tableHeader, 0);
    JTable table = new JTable(tableModel) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; //aby tabela była nieedytowalna
        }
    };
    JScrollPane scrollPane = new JScrollPane(table);
    JButton buttonNew = new JButton("Nowa kolekcja");
    JButton buttonEdit = new JButton("Edytuj wybrana kolekcje");
    JButton buttonDelete = new JButton("Usun wybrana kolekcje");
    JButton buttonLoad = new JButton("Wczytaj kolekcje");
    JButton buttonLoadAll = new JButton("Wczytaj liste kolekcji");
    JButton buttonSave = new JButton("Zapisz wybrana kolekcje");
    JButton buttonSaveAll = new JButton("Zapisz wszystkie kolekcje");
    JButton buttonUnion = new JButton("Suma kolekcji");
    JButton buttonIntersection = new JButton("Iloczyn kolekcji");
    JButton buttonDifference = new JButton("Roznica kolekcji");
    JButton buttonSymmetricDifference = new JButton("Roznica symetryczna kolekcji");
    JButton buttonInfo = new JButton("Info");
    JButton buttonExit = new JButton("Wyjdz");

    public AppWindow() {
        super("Menedzer kolekcji plakatow");
        setSize(1024, 768);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //dodanie wszystkich action listenerów
        menuNew.addActionListener(this);
        menuEdit.addActionListener(this);
        menuDelete.addActionListener(this);
        menuLoad.addActionListener(this);
        menuLoadAll.addActionListener(this);
        menuSave.addActionListener(this);
        menuSaveAll.addActionListener(this);
        menuUnion.addActionListener(this);
        menuIntersection.addActionListener(this);
        menuDifference.addActionListener(this);
        menuSymmetricDifference.addActionListener(this);
        menuInfo.addActionListener(this);
        menuExit.addActionListener(this);

        buttonNew.addActionListener(this);
        buttonEdit.addActionListener(this);
        buttonDelete.addActionListener(this);
        buttonLoad.addActionListener(this);
        buttonLoadAll.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonSaveAll.addActionListener(this);
        buttonUnion.addActionListener(this);
        buttonIntersection.addActionListener(this);
        buttonDifference.addActionListener(this);
        buttonSymmetricDifference.addActionListener(this);
        buttonInfo.addActionListener(this);
        buttonExit.addActionListener(this);

        //dodanie menu
        setJMenuBar(menuBar);
        menuBar.add(menuCollection);
        menuBar.add(menuSpecialCollection);
        menuBar.add(menuProgram);
        menuCollection.add(menuNew);
        menuCollection.add(menuEdit);
        menuCollection.add(menuDelete);
        menuCollection.add(menuLoad);
        menuCollection.add(menuLoadAll);
        menuCollection.add(menuSave);
        menuCollection.add(menuSaveAll);
        menuSpecialCollection.add(menuUnion);
        menuSpecialCollection.add(menuIntersection);
        menuSpecialCollection.add(menuDifference);
        menuSpecialCollection.add(menuSymmetricDifference);
        menuProgram.add(menuInfo);
        menuProgram.add(menuExit);

        //dodanie głównego okna aplikacji
        scrollPane.setPreferredSize(new Dimension(this.getWidth() - 50, this.getHeight() - 140));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Zbior kolekcji"));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        mainPanel.add(scrollPane);
        mainPanel.add(buttonNew);
        mainPanel.add(buttonEdit);
        mainPanel.add(buttonDelete);
        mainPanel.add(buttonLoad);
        mainPanel.add(buttonLoadAll);
        mainPanel.add(buttonSave);
        mainPanel.add(buttonSaveAll);
        mainPanel.add(buttonUnion);
        mainPanel.add(buttonIntersection);
        mainPanel.add(buttonDifference);
        mainPanel.add(buttonSymmetricDifference);
        mainPanel.add(buttonInfo);
        mainPanel.add(buttonExit);

        setContentPane(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object sourceOfEvent = e.getSource();
        if (sourceOfEvent == menuNew || sourceOfEvent == buttonNew) {
            newCollection();
        } else if (sourceOfEvent == menuEdit || sourceOfEvent == buttonEdit) {
            editCollection();
        } else if (sourceOfEvent == menuDelete || sourceOfEvent == buttonDelete) {
            deleteCollection();
        } else if (sourceOfEvent == menuLoad || sourceOfEvent == buttonLoad) {
            loadOneCollection();
        } else if (sourceOfEvent == menuLoadAll || sourceOfEvent == buttonLoadAll) {
            loadManyCollections();
        } else if (sourceOfEvent == menuSave || sourceOfEvent == buttonSave) {
            saveSelectedCollection();
        } else if (sourceOfEvent == menuSaveAll || sourceOfEvent == buttonSaveAll) {
            saveAllCollections();
        } else if (sourceOfEvent == menuUnion || sourceOfEvent == buttonUnion) {
            unionCollection();
        } else if (sourceOfEvent == menuIntersection || sourceOfEvent == buttonIntersection) {
            intersectionCollection();
        } else if (sourceOfEvent == menuDifference || sourceOfEvent == buttonDifference) {
            differenceCollection();
        } else if (sourceOfEvent == menuSymmetricDifference || sourceOfEvent == buttonSymmetricDifference) {
            symmetricDifferenceCollection();
        } else if (sourceOfEvent == menuInfo || sourceOfEvent == buttonInfo) {
            info();
        } else if (sourceOfEvent == menuExit || sourceOfEvent == buttonExit) {
            exit();
        }
    }

    void addCollectionToTable(CollectionOfPosters collection) {
        String[] data = {collection.getCollectionName(), String.valueOf(collection.getCollectionType()), String.valueOf(collection.size())};
        tableModel.addRow(data);
    }

    void editCollectionInTable(int index, CollectionOfPosters editedCollection) {
        tableModel.setValueAt(editedCollection.getCollectionName(), index, 0);
        tableModel.setValueAt(editedCollection.getCollectionType(), index, 1);
        tableModel.setValueAt(editedCollection.size(), index, 2);
    }

    void newCollection() {
        String collectionName = JOptionPane.showInputDialog("Podaj nazwe kolekcji");
        CollectionType collectionType = (CollectionType) JOptionPane.showInputDialog(this, "Wybierz typ kolekcji", "", JOptionPane.INFORMATION_MESSAGE, null, CollectionType.values(), CollectionType.VECTOR);
        CollectionOfPosters newCollection;
        try {
            newCollection = CollectionApp.newCollection(collectionName, collectionType);
            CollectionWindow window = new CollectionWindow(this, newCollection);
            addCollectionToTable(newCollection);
        } catch (InvalidNameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    void editCollection() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            CollectionOfPosters editedCollection = CollectionApp.getAppCollections().get(index);
            CollectionWindow window = new CollectionWindow(this, editedCollection);
            editCollectionInTable(index, editedCollection);
        }
    }

    void deleteCollection() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            CollectionApp.deleteCollection(index);
            tableModel.removeRow(index);
        }
    }

    void loadOneCollection() {
        String filename = chosenFile();
        if (filename == null) {
            return;
        }
        try {
            CollectionOfPosters toAdd = CollectionApp.loadCollection(filename);
            addCollectionToTable(toAdd);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
        }
    }

    void loadManyCollections() {
        String filename = chosenFile();
        if (filename == null) {
            return;
        }
        try {
            ArrayList<CollectionOfPosters> toAdd = CollectionApp.loadAListOfCollectionsFromFile(filename);
            for (CollectionOfPosters i : toAdd) {
                addCollectionToTable(i);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
        }
    }

    void saveSelectedCollection() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            String filename = chosenFile();
            if (filename == null) {
                return;
            }
            try {
                CollectionApp.saveCollection(filename, index);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void saveAllCollections() {
        String filename = chosenFile();
        if (filename == null) {
            return;
        }
        try {
            CollectionApp.saveTheListOfCollectionsToFile(filename);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Blad", JOptionPane.ERROR_MESSAGE);
        }
    }

    void unionCollection() {
        //TODO
    }

    void intersectionCollection() {
        //TODO
    }

    void differenceCollection() {
        //TODO
    }

    void symmetricDifferenceCollection() {
        //TODO
    }

    void exit() {
        if (JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz wyjsc z programu?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}