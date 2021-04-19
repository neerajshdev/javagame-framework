package javagames.example.ch9;

import java.io.*;

public class ReadingDataFromFiles {

    public void runTest() {
        String path = "res/lorem-ipsum.txt";
        File lorem = new File(path);
        readInBytes(lorem);
        readInText(lorem);
    }

    public void readInBytes(File file) {
        try(FileInputStream fileInputStream = new FileInputStream(file)) {

            System.out.println();
            System.out.println();
            System.out.println( "********************");
            System.out.println( "Reading in bytes" );
            System.out.println();

            int data = fileInputStream.read();
            StringBuilder str = new StringBuilder();
            while (data != -1) {
                char ch = (char) data;
                str.append(ch);
                data = fileInputStream.read();
            }
            System.out.println(str.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInText(File file) {
        System.out.println();
        System.out.println();
        System.out.println( "********************");
        System.out.println( "Reading in Texts" );
        System.out.println();

        try (BufferedReader buffReader = new BufferedReader(
                new FileReader(file)
        )){
            String line = buffReader.readLine();
            StringBuilder str = new StringBuilder();
            while (line != null) {
                str.append(line).append("\n");
                line = buffReader.readLine();
            }
            System.out.println(str.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        new ReadingDataFromFiles().runTest();
    }
}
