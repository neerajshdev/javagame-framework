package javagames.example.ch12;

public class WaitNotifyExample implements FakeHardwareListener{


    public WaitNotifyExample() {

    }

    public void runTest() throws InterruptedException {

        FakeHardware hardware = new FakeHardware("FakeHardware");
        hardware.addListener(this);
        synchronized (this) {
            hardware.turnOn();
            while (!hardware.isOn()) {
                wait();
            }
        }

        System.out.println("Hardware is on!");

        synchronized (this) {
            hardware.start(1000, 4);
            while ( !hardware.isRunning() ){
                wait();
            }
        }
        System.out.println("Hardware is running!");

        synchronized (this) {
            while (hardware.isRunning()) {
                wait();
            }
        }
        System.out.println("Hardware has stopped!");

        synchronized (this) {
            hardware.turnOff();
            while (hardware.isOn()) {
                wait();
            }
        }
        System.out.println("Hardware has turned off");
    }

    public synchronized void event(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
        synchronized (this) {
            System.out.println("Got Event: " + event);
            notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new WaitNotifyExample().runTest();
    }
}
