package com.refactoringMatcher.representation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.lang.*;

public class GroumTryCatchTest {

    public static void TryCatchTestMethod() {
        Scanner keys = new Scanner(System.in);
        for (;;) {
            String nameOfFile = keys.nextLine().trim();
            if (nameOfFile.equalsIgnoreCase("quit")) {
                break;
            }
            File f = new File(nameOfFile);
            if (f.exists()) {
                if (f.isFile() && f.canRead()) {
                    Scanner input = null;
                    try {
                        input = new Scanner(f);
                        while (input.hasNextLine()) {
                            String contents = input.nextLine();
                            System.out.println(contents);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (input != null) {
                            input.close();
                        }
                    }
                } else if (f.isDirectory()) {
                    try {
                        System.out.println("File "
                                + f.getCanonicalPath()
                                + " is a directory");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
