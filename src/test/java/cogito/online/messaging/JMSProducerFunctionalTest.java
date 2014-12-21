package cogito.online.messaging;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>JMSProducerFunctionalTest</code> is used to create and send XML via JMS
 * 
 * Make sure activeMQ Broker is running and packages queue exists
 * 
 * @author jdeane
 * @version 1.0
 */
public class JMSProducerFunctionalTest extends TestCase {
	
	private static final Logger logger = LoggerFactory.getLogger
			(JMSProducerFunctionalTest.class);	
    
    private ActiveMQConnection connection;
    private MessageProducer producer;
    private QueueSession session;
    
    protected void setUp() throws Exception {  
        connection = ActiveMQConnection.makeConnection("tcp://localhost:61616");
        connection.start();
        
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("test.alchemy");
        producer = session.createProducer(destination);
    }

    protected void tearDown() throws Exception {          
		producer.close();
		session.close();
        connection.close();
    }
    
    /**
     * Sends Magic Order to Test Queue
     * @throws Exception
     */
    public void testCreatePackage() throws Exception {
        
        //Create message
        TextMessage textMessage = session.createTextMessage();
        
        //set the body
        String xml = "<order id=\"X1355\" customer=\"Palmer\" item=\"Rainbow Scarf\" amount=\"6\"/>";
        textMessage.setText(xml);
        
        //set custom properties
        textMessage.setStringProperty("token", "6gj#dga99jf");
        textMessage.setStringProperty("company", "Aladin's Magic Shop");
        
        producer.send(textMessage);
        
        logger.debug("Sent test order to queue : \n" + xml);
    }
}   