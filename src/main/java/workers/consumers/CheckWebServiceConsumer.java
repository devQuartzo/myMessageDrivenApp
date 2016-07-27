package workers.consumers;

import com.amazonaws.services.sqs.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workers.AWSQueueServiceUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by victoraldir on 19/07/2016.
 */
public class CheckWebServiceConsumer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(CheckWebServiceConsumer.class);
    private AWSQueueServiceUtil awsQueueServiceUtil;
    private String myQueueUrl;


    public CheckWebServiceConsumer(String myQueueUrl) {
        this.myQueueUrl = myQueueUrl;
        this.awsQueueServiceUtil = AWSQueueServiceUtil.getInstance();
    }

    public void run() {

        // Receive messages
        while (true){

            logger.info("CheckWebServiceConsumer ID {} is listening the queue {}.",Thread.currentThread().getId(), myQueueUrl);

            List<Message> messages = awsQueueServiceUtil.getMessagesFromQueue(myQueueUrl);

            if(messages == null || messages.size() == 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }else{

                Message messageIn = null;

                for (Message message : messages) {
                    logger.info("  Message");
                    logger.info("    MessageId:     " + message.getMessageId());
                    logger.info("    ReceiptHandle: " + message.getReceiptHandle());
                    logger.info("    MD5OfBody:     " + message.getMD5OfBody());
                    logger.info("    Body:          " + message.getBody());
                    messageIn = message;
                    for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                        logger.info("  Attribute");
                        logger.info("    Name:  " + entry.getKey());
                        logger.info("    Value: " + entry.getValue());
                    }

                }

                logger.info("MessageId {} is being deleted ",messageIn.getMessageId());

                AWSQueueServiceUtil.getInstance().deleteMessageFromQueue(myQueueUrl, messageIn);

            }

        }

    }

}
