package workers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workers.consumers.CheckWebServiceConsumer;
import workers.consumers.NotifyConsumer;
import workers.consumers.PersistConsumer;
import workers.producers.StartProcessProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by victoraldir on 19/07/2016.
 */
public class MyMessageDrivenApp {

    private static final String APP_QUEUE = "MyQueue";
    private static Logger logger = LoggerFactory.getLogger(StartProcessProducer.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
    private static AWSCredentials credentials = null;
    private static AmazonSQS sqs;
    private static Region usEast1;
    private static String myQueueUrl;

    public static void main(String[] args) {

        initSQS();
        createQueue();


        StartProcessProducer startProcessProducer = new StartProcessProducer(myQueueUrl);

        NotifyConsumer notifyConsumer = new NotifyConsumer(myQueueUrl);
        CheckWebServiceConsumer checkWebServiceConsumer = new CheckWebServiceConsumer(myQueueUrl);
        PersistConsumer persistConsumer = new PersistConsumer(myQueueUrl);

        executor.execute(startProcessProducer);

    }

    public static void initSQS() {

        logger.debug("===========================================");
        logger.debug("Starting Amazon SQS");
        logger.debug("===========================================");


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

    public static void createQueue() {

        try {

            // Create a queue
            logger.debug("Creating a new SQS queue called {}.", APP_QUEUE);
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(APP_QUEUE);
            myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

            // List queues
            logger.debug("Listing all queues in your account.");
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                logger.debug("  QueueUrl: " + queueUrl);
            }

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }
    }

}
