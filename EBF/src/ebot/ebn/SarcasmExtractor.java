package ebot.ebn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SarcasmExtractor {
	public static void main(String[] args) throws IOException {
		Scanner s = new Scanner(new BufferedInputStream(new FileInputStream(new File("train-balanced-sarcasm.csv"))));
		
		PrintStream sarcasmP = new PrintStream(new BufferedOutputStream(new FileOutputStream("sarcasmP.txt", true)));
		PrintStream sarcasmN = new PrintStream(new BufferedOutputStream(new FileOutputStream("sarcasmN.txt", true)));
		
		int index = 0;
		while (s.hasNextLine() && index < 1000000) {
			String line = s.nextLine();
			
			List<String> data = parseLine(line);
			
			System.out.println(data.get(1));
			
			if (!data.get(0).equals("label")) {
				if (data.get(0).equals("1")) {
					sarcasmP.println(data.get(1));
				} else {
					sarcasmN.println(data.get(1));
				}
			}
			
			index += 1;
		}
		
		sarcasmP.flush();
		sarcasmP.close();
		
		sarcasmN.flush();
		sarcasmN.close();
		
		s.close();
	}
	
	public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, ',', '"');
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, '"');
    }

    @SuppressWarnings("null")
	public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = '"';
        }

        if (separators == ' ') {
            separators = ',';
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }
}
