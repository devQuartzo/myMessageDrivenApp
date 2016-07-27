package workers.producers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workers.AWSQueueServiceUtil;

/**
 * Created by victoraldir on 19/07/2016.
 */
public class StartProcessProducer implements Runnable {

    private String myQueueUrl;
    private AWSQueueServiceUtil awsQueueServiceUtil;
    private Logger logger = LoggerFactory.getLogger(StartProcessProducer.class);
    private int interactions = 10;

    public StartProcessProducer(String myQueueUrl){

        this.myQueueUrl = myQueueUrl;
        this.awsQueueServiceUtil = AWSQueueServiceUtil.getInstance();

    }

    public void run() {

        long time = 5000;
        long id = Thread.currentThread().getId();

        for (int i = 0; i < interactions; i++) {

            logger.info("StartProcessProducer of id {} is running. Waiting {} second to publish a message...", id, time/1000);

            delay(time);

            awsQueueServiceUtil.sendMessageToQueue(myQueueUrl, "This is a message from thread " + id);

            logger.info("A message has been sent to {} from threadId {}", myQueueUrl, Thread.currentThread().getId());
        }

    }


    public void delay(long time){
        try{
            Thread.sleep(time);
        }catch (Exception ex){

        }
    }
}
