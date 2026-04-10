package org.neomin.interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final Tokenizer tokenizer = new Tokenizer();
        readScripts(tokenizer);

        tokenizer.start();
    }

    public static void readScripts(Tokenizer tokenizer) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/script.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                tokenizer.insertExecutionLine(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

