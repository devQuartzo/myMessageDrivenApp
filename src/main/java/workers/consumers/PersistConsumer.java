package workers.consumers;

/**
 * Created by victoraldir on 19/07/2016.
 */
public class PersistConsumer implements Runnable {

    private String myQueueUrl;

    public PersistConsumer(String myQueueUrl) {
        this.myQueueUrl = myQueueUrl;
    }

    public void run() {

    }
}
