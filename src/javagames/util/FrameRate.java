package javagames.util;

public class FrameRate {
    private String fps;
    private long lastTime;
    private long deltaTime;
    private int count;


    public void initialize(){
        fps = "FPS: 0";
        lastTime = System.nanoTime();
        deltaTime = 0;
        count = 0;
    }

    public void calculate(){
        long currentTime = System.nanoTime();
        deltaTime += currentTime - lastTime;

        count++;
        // update the fps value after 1 sec
        if (deltaTime > 1E9){
            deltaTime -= 1E9;
            fps = "FPS: " + count;
            count = 0;
        }

        lastTime = currentTime;
    }

    public String getFps() {
        return fps;
    }
}
