package javagames.example.ch9;


import java.io.*;
import java.util.Random;

public class WritingDataToFiles {
    private Random rand;

    public void runTest() {
        rand = new Random();
        File file = new File("res/write-bytes.txt");
        File fileText = new File("res/write-Texts.txt");
        writeBytes(file);
        writeTexts(fileText);

    }

    public void writeBytes(File file) {
        System.out.println();
        System.out.println();
        System.out.println("********************");
        System.out.println(file.getName());

        try (FileOutputStream fos = new FileOutputStream(file, true)){
            for (int i = 0; i < 1000; i++) {
                fos.write(rand.nextInt(256));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Wrote: " + file.getPath());
    }

    public void writeTexts(File file) {
        System.out.println();
        System.out.println();
        System.out.println("********************");
        System.out.println(file.getName());

        try(PrintWriter printWriter = new PrintWriter(file)) {
            String[] strings = {
                    "Lorem ipsum dolor sit amet, consectetur adipisicing elit,",
                    "sed do eiusmod tempor incididunt ut labore et dolore magna",
                    "aliqua. Ut enim ad minim veniam, quis nostrud exercitation",
                    "ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                    "Duis aute irure dolor in reprehenderit in voluptate velit",
                    "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint",
                    "occaecat cupidatat non proident, sunt in culpa qui officia",
                    "deserunt mollit anim id est laborum.",
            };

            printWriter.append("\n\n");
            for (String str : strings) {
                printWriter.println(str);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
       new WritingDataToFiles();
    }
}
