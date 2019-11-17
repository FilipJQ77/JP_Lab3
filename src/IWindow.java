/*
    Program: Aplikacja z graficznym interfejsem i operacjami na kolekcjach
    Plik: IWindow.java
    Autor: Filip Przygoński, 248892
    Data: Listopad 2019
*/

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * interfejs do okienek
 */
public interface IWindow {

    String INFO = "Aplikacja z graficznym interfejsem i operacjami na kolekcjach\nAutor: Filip Przygonski, 248892\nData: Listopad 2019";

    /**
     * zwraca scieżkę do wybranego pliku
     * @return
     */
    default String chosenFile(/*String[] typesOfAcceptedFiles*/) {
        JFileChooser fileChooser = new JFileChooser();
        /*FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                for(String i : typesOfAcceptedFiles){
                    if(pathname.getName().endsWith(i)){
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                StringBuilder possibleFiles = new StringBuilder();
                possibleFiles.append("Typ pliku: ");
                for(String i : typesOfAcceptedFiles){
                    possibleFiles.append(i).append(", ");
                }
                possibleFiles.replace(possibleFiles.length()-1,possibleFiles.length(),"");
                return possibleFiles.toString();
            }
        };
        fileChooser.setFileFilter(fileFilter);*/
        File file = null;
        if (fileChooser.showOpenDialog((Component) this) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        return (file != null) ? file.getName() : null;
    }

    /**
     * wyświetla informacje o autorze
     */
    default void info() {
        JOptionPane.showMessageDialog((Component) this, INFO, "Informacje", JOptionPane.INFORMATION_MESSAGE);
    }
}
