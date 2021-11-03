package usergenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    protected int getIntOfRange(int by, int to) {
        return (int) (by + Math.random() * (to - by));
    }

    protected double getDoubleOfRange(double by, double to) {
        return (by + Math.random() * (to - by));
    }

    protected Object getOne(List<?> lst) {
        int integer = (int) (Math.random() * (double) lst.size());

        return lst.get(integer);
    }

    protected List<String> readFile(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader);

            List<String> listLines = new ArrayList<>();
            while (reader.ready()) {
                listLines.add(reader.readLine());
            }

            return listLines;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
