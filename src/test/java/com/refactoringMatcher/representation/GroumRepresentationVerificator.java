package com.refactoringMatcher.representation;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroumRepresentationVerificator {
    static public Set<String> groumNodesOfPaperExample = Stream.of(
            "1 StringBuffer.<init>"
            , "3 FileReader.<init>"
            , "2 BufferedReader.<init>"
            , "5 BufferedReader.readLine"
            , "4 WHILE"
            , "6 StringBuffer.append"
            , "8 StringBuffer.length"
            , "7 IF"
            , "10 StringBuffer.toString"
            , "11 BufferedReader.close").collect(Collectors.toSet());
    static public Set<String> groumEdgesOfPaperExample = Stream.of(
            "1 StringBuffer.<init>-->3 FileReader.<init>\n"
            , "1 StringBuffer.<init>-->6 StringBuffer.append\n"
            , "1 StringBuffer.<init>-->8 StringBuffer.length\n"
            , "1 StringBuffer.<init>-->10 StringBuffer.toString\n"
            , "1 StringBuffer.<init>-->2 BufferedReader.<init>\n"
            , "3 FileReader.<init>-->2 BufferedReader.<init>\n"
            , "2 BufferedReader.<init>-->5 BufferedReader.readLine\n"
            , "2 BufferedReader.<init>-->11 BufferedReader.close\n"
            , "5 BufferedReader.readLine-->4 WHILE\n"
            , "5 BufferedReader.readLine-->11 BufferedReader.close\n"
            , "5 BufferedReader.readLine-->6 StringBuffer.append\n"
            , "4 WHILE-->6 StringBuffer.append\n"
            , "4 WHILE-->7 IF\n"
            , "6 StringBuffer.append-->8 StringBuffer.length\n"
            , "6 StringBuffer.append-->10 StringBuffer.toString\n"
            , "8 StringBuffer.length-->7 IF\n"
            , "8 StringBuffer.length-->10 StringBuffer.toString\n"
            , "7 IF-->10 StringBuffer.toString\n"
            , "10 StringBuffer.toString-->11 BufferedReader.close\n"
    ).collect(Collectors.toSet());
}
