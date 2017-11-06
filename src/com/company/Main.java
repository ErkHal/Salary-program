package com.company;

import CSVUtils.CSVUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        while (true) {

            Scanner scan = new Scanner(System.in);

            try {

                JFileChooser selectCSV = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        ".CSV Files", "csv");
                selectCSV.setFileFilter(filter);

                int returnVal = selectCSV.showOpenDialog(new JPanel());

                CSVUtils parser = new CSVUtils();

                ArrayList<String[]> entries = new ArrayList<>();

                try {

                    entries = parser.parseFile(selectCSV.getSelectedFile(), true, ',');

                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                }

                String[] entry = entries.get(23);

                String[] setit = parser.calculateWorkShift(entry);

                System.out.println("###" + parser.calculateRegDailyWage(new int[] {10,15}));

                System.out.println(setit[0]);

            } catch (Exception e) {
                System.out.println("Error while processing the file. Try again ? Y/N");
                String input = scan.nextLine();
                if(input.equals("N") || input.equals("n")) {
                    System.exit(1);
                }
            }
        }
    }
}
