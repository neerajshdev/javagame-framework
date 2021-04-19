package javagames.example.ch9;
import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.Vector;

public class FilesAndDirectories {


    public FilesAndDirectories() {

    }


    public void runTest() {
        String dir = "C:\\Users\\nsxyl\\Documents\\PDF BOOKS";
        File file = new File( dir );
        printTree(file);
    }


    public void printTree(File dir) {
        ArrayWrapper<File> files = new ArrayWrapper<>(dir.listFiles());
        Stack<ArrayWrapper<File>> depthStack =  new Stack<>();
        depthStack.add(files);

        int depth;

        do {
            depth = depthStack.size();

            ArrayWrapper<File> obj = depthStack.peek();

            while (obj.hasNext()) {

                StringBuilder str;
                str = new StringBuilder();
                for (int i = 1; i < depth; i++ ) {
                    str.append("| ");
                }

                File file = obj.getNext();
                if (file.isDirectory()) {
                    str.append("+ ");
                    str.append(file.getName());
                    System.out.println(str.toString());
                    depthStack.push(new ArrayWrapper<>(file.listFiles()));
                    break;
                } else {
                    str.append("| ").append(file.getName());
                    System.out.println(str.toString());
                }
            }

            if (obj.isProcessed()) {
                depthStack.remove(obj);
            }
        } while (!depthStack.empty());
    }


    public static void main( String[] args ) {
        new FilesAndDirectories().runTest();
    }
}

class ArrayWrapper<E> {
    private int next;
    private E[] array;
    private int length;

    public ArrayWrapper(E[] array) {
        this.array = array;
        this.length = array.length;
    }

    public boolean hasNext() {
        return next < length;
    }

    public E getNext() {
        return array[next++];
    }

    public boolean isProcessed() {
        return next == length;
    }
}