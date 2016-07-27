package workers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by victoraldir on 25/07/2016.
 */
public class AWSQueueServiceUtil {

    private AWSCredentials credentials;
    private AmazonSQS sqs;
    private static Region usEast1;
    private static volatile  AWSQueueServiceUtil awssqsUtil = new AWSQueueServiceUtil();
    private static Logger logger = LoggerFactory.getLogger(AWSQueueServiceUtil.class);


    private   AWSQueueServiceUtil(){
        try{
            credentials = new ProfileCredentialsProvider().getCredentials();
            sqs = new AmazonSQSClient(credentials);
            usEast1 = Region.getRegion(Regions.US_EAST_1);
            sqs.setRegion(usEast1);

        }catch(Exception e){
            logger.error("exception while creating awss3client : " + e);
        }
    }

    public static AWSQueueServiceUtil getInstance(){
        return awssqsUtil;
    }

    public AmazonSQS getAWSSQSClient(){
        return awssqsUtil.sqs;
    }

    /**
     * Creates a queue in your region and returns the url of the queue
     * @param queueName
     * @return
     */
    public String createQueue(String queueName){
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        return  sqs.createQueue(createQueueRequest).getQueueUrl();
    }

    /**
     * returns the queueurl for for sqs queue if you pass in a name
     * @param queueName
     * @return
     */
    public String getQueueUrl(String queueName){
        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
        return this.sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
    }

    /**
     * lists all your queue.
     * @return
     */
    public ListQueuesResult listQueues(){
        return this.sqs.listQueues();
    }

    /**
     * send a single message to your sqs queue
     * @param queueUrl
     * @param message
     */
    public void sendMessageToQueue(String queueUrl, String message){
        SendMessageResult messageResult =  this.sqs.sendMessage(new SendMessageRequest(queueUrl, message));
        logger.info("AWSQueueServiceUtil.sendMessageToQueue: message result -> {}",messageResult.toString());
    }

    /**
     * gets messages from your queue
     * @param queueUrl
     * @return
     */
    public List<Message> getMessagesFromQueue(String queueUrl){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    /**
     * deletes a single message from your queue.
     * @param queueUrl
     * @param message
     */
    public void deleteMessageFromQueue(String queueUrl, Message message){
        String messageRecieptHandle = message.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
    }

    /**
     * deletes a queue.
     * @param queueUrl
     */
    public void deleteQueue(String queueUrl){
        sqs.deleteQueue(new DeleteQueueRequest(queueUrl));
    }

}
