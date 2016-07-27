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

    private static final String QUEUE_WS_CHECK = "quartzo_myMessageApp_version_doWSCheck";
    private static final String QUEUE_DO_NOTIFICATION = "quartzo_myMessageApp_version_doNotification";
    private static final String QUEUE_DO_PERSISTENCE = "quartzo_myMessageApp_version_doPersistence";
    private static Logger logger = LoggerFactory.getLogger(StartProcessProducer.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
    private static AWSCredentials credentials = null;
    private static AmazonSQS sqs;
    private static Region usEast1;
    private static String myQueueUrl;
    private static AWSQueueServiceUtil awsQueueServiceUtil = AWSQueueServiceUtil.getInstance();

    public static void main(String[] args) {

        createQueues();

        //Creates 3 different producers with 5 interactions
        StartProcessProducer startProcessProducer1 = new StartProcessProducer(QUEUE_WS_CHECK);
        StartProcessProducer startProcessProducer2 = new StartProcessProducer(QUEUE_WS_CHECK);
        StartProcessProducer startProcessProducer3 = new StartProcessProducer(QUEUE_WS_CHECK);

        executor.execute(startProcessProducer1);
        executor.execute(startProcessProducer2);
        executor.execute(startProcessProducer3);

        //Launches 2 different consumers
        CheckWebServiceConsumer checkWebServiceConsumer1 = new CheckWebServiceConsumer(QUEUE_WS_CHECK);
        CheckWebServiceConsumer checkWebServiceConsumer2 = new CheckWebServiceConsumer(QUEUE_WS_CHECK);

        executor.execute(checkWebServiceConsumer1);
        executor.execute(checkWebServiceConsumer2);

    }

    public static void createQueues(){
        awsQueueServiceUtil.createQueue(QUEUE_WS_CHECK);
        awsQueueServiceUtil.createQueue(QUEUE_DO_NOTIFICATION);
        awsQueueServiceUtil.createQueue(QUEUE_DO_PERSISTENCE);
    }


}
