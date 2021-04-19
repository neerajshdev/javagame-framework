package javagames.example.ch12;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;

public class CallableTaskExample  implements Callable<Boolean> {
    @Override
    public Boolean call() throws Exception {
        Random random = new Random();
        int random_int = random.nextInt(6);
        if ( random_int == 0 ) {
            throw new RuntimeException ( "I love the new thread stuff!!! :)" );
        }
        Thread.sleep( random_int * 100 );
        return random_int % 2 == 0;
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < 50; i++) {
                try {
                    Future<Boolean> result = service.submit(new CallableTaskExample());
                    boolean success = result.get();
                    System.out.println( "Result: " + success );
                } catch (InterruptedException e) {
                    System.out.println( "Awesome! Thread was canceled " );
                } catch (ExecutionException e) {
                    Throwable throwable = e.getCause();
                    System.out.println( "Error: " + throwable.getMessage() );
                }
            }
        } finally {
            System.out.println( new Date() );
            service.shutdown();
            try {
                service.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit( -1 );
            }
        }

    }
}
