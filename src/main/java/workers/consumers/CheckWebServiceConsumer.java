package workers.consumers;

/**
 * Created by victoraldir on 19/07/2016.
 */
public class CheckWebServiceConsumer implements Runnable {

    private String myQueueUrl;

    public CheckWebServiceConsumer(String myQueueUrl) {
        this.myQueueUrl = myQueueUrl;
    }

    public void run() {

    }
}
