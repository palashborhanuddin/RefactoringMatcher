package com.refactoringMatcher.targets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TargetClass {

    private static void groumTestMethod() throws IOException {
        StringBuffer strbuf = new StringBuffer();
        BufferedReader in = new BufferedReader(new FileReader(""));
        String str;
        while((str = in.readLine()) != null ) {
            strbuf.append(str + "\n");
        }
        if(strbuf.length() > 0)
            outputMessage(strbuf.toString());
        in.close();
    }

    private static void outputMessage(String s) {
    }
}
