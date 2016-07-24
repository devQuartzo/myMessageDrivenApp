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

/**
 * Created by victoraldir on 19/07/2016.
 */
public class StartProcessProducer implements Runnable {

    private String myQueueUrl;
    private AWSCredentials credentials;
    private AmazonSQS sqs;
    private Region usEast1;
    private Logger logger = LoggerFactory.getLogger(StartProcessProducer.class);

    public StartProcessProducer(String myQueueUrl){

        this.myQueueUrl = myQueueUrl;
        initSQS();

    }

    public void run() {
        logger.debug("StartProcessProducer is running. Publishing message...");

        sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));

        logger.debug("A message has been sent to {} ", myQueueUrl);
    }

    public void initSQS(){
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
            sqs = new AmazonSQSClient(credentials);
            usEast1 = Region.getRegion(Regions.US_EAST_1);
            sqs.setRegion(usEast1);

        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
    }
}
