package workers.consumers;

/**
 * Created by victoraldir on 19/07/2016.
 */
public class NotifyConsumer implements Runnable {

    private String myQueueUrl;

    public NotifyConsumer(String myQueueUrl) {
        this.myQueueUrl = myQueueUrl;
    }

    public void run() {

    }
}
