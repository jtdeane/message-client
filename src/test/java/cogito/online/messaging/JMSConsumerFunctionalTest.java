package cogito.online.messaging;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>JMSConsumerFunctionalTest</code> is used to receive xml from a queue
 * 
 * Make sure activeMQ Broker is running and packages queue exists
 * 
 * @author jdeane
 * @version 1.0
 */
public class JMSConsumerFunctionalTest extends TestCase {
    
    private ActiveMQConnection connection;
    private Session session;
    private MessageConsumer consumer;
    
	private static final Logger logger = LoggerFactory.getLogger
			(JMSConsumerFunctionalTest.class);    
    
    protected void setUp() throws Exception {   
        connection = ActiveMQConnection.makeConnection("tcp://localhost:61616");
        connection.start();
        
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("test.alchemy");
    	consumer = session.createConsumer(destination);
    }

    protected void tearDown() throws Exception {  
		consumer.close();
    	session.close();
        connection.close();
    }
    
    /**
     * Creates Crayon Package
     * @throws Exception
     */
    public void testReceivePackage() throws Exception { 
    	
    	Message message = consumer.receive();
    	
    	logger.debug("Received message: \n" + message);
    }
}   