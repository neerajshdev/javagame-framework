package javagames.example.ch12;

import javagames.util.SafeKeyboardFramework;
import javagames.util.SimpleFramework;
import javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class FileLoadingExample extends SimpleFramework {

    private static final int NUMBER_OF_FILES = 100;
    private ExecutorService singleThread = Executors.newSingleThreadExecutor();
    private ExecutorService thirtyTwoThreads = Executors.newFixedThreadPool( 32 );
    private ExecutorService unlimitedThreads = Executors.newCachedThreadPool();

    private boolean loading = false;
    private List<Callable<Boolean>> fileTasks;
    private List<Future<Boolean>> fileResults;

    public FileLoadingExample() {
        appTitle = "File loading simulation";
        appBackground = Color.WHITE;
        appWidth = 640;
        appHeight = 640;
        appFPSColor = Color.black;
        appSleep = 1L;
    }

    @Override
    protected void initialize() {
        super.initialize();
        fileTasks = new ArrayList<Callable<Boolean>>();
        fileResults = new ArrayList<Future<Boolean>>();

        for (int i = 0; i<NUMBER_OF_FILES; i++) {
            int taskNumber = i;
            fileTasks.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    // pretend to load a file
                    Thread.sleep( new Random().nextInt(700) );
                    System.out.println( "Task : " + taskNumber );
                    return Boolean.TRUE;
                }
            });
        }
    }

    @Override
    protected void processInput(float t) {
        super.processInput(t);

        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            if (!loading) {
                for (Callable<Boolean> task : fileTasks) {
                    fileResults.add(singleThread.submit(task));
                }
            }
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            if (!loading) {
                for (Callable<Boolean> task : fileTasks) {
                    fileResults.add(thirtyTwoThreads.submit(task));
                }
            }
        }

        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            if (!loading) {
                for (Callable<Boolean> task : fileTasks) {
                    fileResults.add(unlimitedThreads.submit(task));
                }
            }
        }
    }

    @Override
    protected void updateObjects(float t) {
        super.updateObjects(t);

        Iterator<Future<Boolean>> it = fileResults.iterator();
        while (it.hasNext()) {
            Future<Boolean> next = it.next();
            if (next.isDone()) {
                try {
                    if (next.get()) {
                        it.remove();
                    }
                } catch (InterruptedException e) {}
                  catch (ExecutionException e) {}
            }
        }
        loading = !fileResults.isEmpty();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);

        textPos = Utility.drawString(g, 20, textPos, "",
                " Press the number key to start loading files",
                "(1) 1 Thread",
                "(2) 32 Threads",
                "(3) Unlimitied Threads",
                "");

        double percentComplete =
                (NUMBER_OF_FILES - fileResults.size()) / (double)NUMBER_OF_FILES;
        String fileProgress =
                String.format( "File Progress: %.0f %%", 100.0 * percentComplete );
        textPos = Utility.drawString( g, 20, textPos, fileProgress );
    }

    @Override
    protected void terminate() {
        super.terminate();
        shutDownExecutor(singleThread);
        shutDownExecutor(thirtyTwoThreads);
        shutDownExecutor(unlimitedThreads);
    }

    private void shutDownExecutor( ExecutorService exec) {
        exec.shutdown();
        try {
            exec.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println(" Executor shutdown !!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launchApp(new FileLoadingExample());
    }
}
