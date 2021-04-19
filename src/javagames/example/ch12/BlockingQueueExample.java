package javagames.example.ch12;

import java.util.Random;
import java.util.concurrent.*;

public class BlockingQueueExample {


    class Producer implements Callable<Void> {
        private Random random = new Random();
        private int sleep;
        private int numberOfMessages;

        public Producer(int numberOfMessages, int sleep) {
            this.sleep = sleep;
            this.numberOfMessages = numberOfMessages;
        }

        @Override
        public Void call() throws Exception {
            for (int i = 1; i <= numberOfMessages; i++) {
                Message[] messages = Message.values();
                int index = random.nextInt(messages.length - 2 );
                queue.put(messages[index]);
                System.out.println("PUT(" + i + ") " + messages[index]);
                sleep(sleep);
            }

            queue.put(Message.POISON_PILL);
            return null;
        }
    }



    class Consumer implements Callable<Integer> {
        int messageCount;

        @Override
        public Integer call() throws Exception {
            while (true) {
                Message message = queue.take();
                messageCount ++;
                System.out.println("Received: " + message);
                if (message == Message.POISON_PILL) {
                    break;
                }
            }
            return messageCount;
        }
    }

    enum Message {
        MESSAGE_ONE,
        MESSAGE_TWO,
        MESSAGE_THREE,
        POISON_PILL; // Quit :)
    }


    private BlockingQueue<Message> queue;
    private ExecutorService exec;

    public BlockingQueueExample () {
        queue = new LinkedBlockingQueue<>();
        exec = Executors.newCachedThreadPool();
    }

    private void runTask() {
        int numberOfMessages = 100;
        int sleep = 100;

        System.out.println("Message sent: " + numberOfMessages);
        exec.submit(new Producer(numberOfMessages, sleep));
        // sleep a little
        sleep(2000);

        try {
            Future<Integer> consumer = exec.submit(new Consumer());
            try {
                System.out.println(consumer.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } finally {
            exec.shutdown();
            try {
                exec.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sleep(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BlockingQueueExample().runTask();
    }

}
