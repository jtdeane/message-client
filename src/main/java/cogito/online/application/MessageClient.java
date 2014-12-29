package cogito.online.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * Client for Enterprise Message Workshop
 * @author jeremydeane
 */
public final class MessageClient {

	private static final Logger logger = LoggerFactory.getLogger
			(MessageClient.class);

	public static void main(String[] args) {
		
		logger.debug("Loading Spring Application Context from Classpath");
		
		ApplicationContext context = new ClassPathXmlApplicationContext
				("message-client-spring.xml");
		
		//argument determines the queue or topic
		if (args.length > 0) {
			
			switch (args[0]) {
			
			case "magic.alerts":
				
				publishToTopic(context);
				
				break;
				
			case "magic.order":
				
				sendSingleOrder(context);
				
				break;
				
			case "magic.orders":
				
				sendBatchOrder("magic.orders", context);
				
				break;
				
			case "emagic.orders":
				
				sendBatchOrder("emagic.orders", context);
				
				break;
				
			case "emagic.bad":
				
				sendBadOrder(context);
				
				break;

			default:
				
				sendAlchemyAlert(context);
				
				break;
			}			
			
		} else {
			
			//no argument passed
			sendAlchemyAlert(context);
		}
	}
	
	/**
	 * Send a Test Alert to a Queue
	 * @param context
	 */
	private static void sendAlchemyAlert (ApplicationContext context) {
		
		logger.debug("Sending Message to Queue: test.alchemy");

		//destination set in message-client-spring.xml
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");
		
		jmsTemplate.send(new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                TextMessage message = session.createTextMessage
                		("Alchemy is forbidden; Magic is encouraged"); 

                message.setStringProperty("Mime Type", "text/html");

                return message;
            }

        });
		
		logger.debug("Check: http://localhost:8161/admin/queues.jsp");
	}
	
	/**
	 * Send a Single Order to a Queue
	 * @param context
	 */
	private static void sendSingleOrder(ApplicationContext context) {
		
		logger.debug("Sending single order to Queue: magic.order");
		
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");
		
		jmsTemplate.send("magic.order", new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                TextMessage message = null;
				
                try {
					
                	message = session.createTextMessage
							(getXmlFromFile("order.xml"));
				
					message.setStringProperty("Mime Type", "application/xml");
				
                } catch (Exception e) {
                
            		logger.error("Unable to retrieve file order.xml: + \n:" 
            				+ e.getMessage());
					
					System.exit(0);
				} 
                
                return message;
            }

        });
		
		logger.debug("Check: http://localhost:8161/admin/queues.jsp");
	}
	
	/**
	 * Send a Batch Order to a Queue
	 * @param context
	 */
	private static void sendBatchOrder(String destination, ApplicationContext context) {
		
		logger.debug("Sending batch order to Queue: " + destination);
		
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");
		
		jmsTemplate.send(destination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                TextMessage message = null;
				
                try {
					
                	message = session.createTextMessage
							(getXmlFromFile("orders.xml"));
				
					message.setStringProperty("Mime Type", "application/xml");
				
                } catch (Exception e) {
                
            		logger.error("Unable to retrieve file order.xml: + \n:" 
            				+ e.getMessage());
					
					System.exit(0);
				} 
                
                return message;
            }

        });
		
		logger.debug("Check: http://localhost:8161/admin/queues.jsp");
	}
	
	/**
	 * Publish Alert to Topic
	 * @param context
	 */
	private static void publishToTopic(ApplicationContext context) {
		
		logger.debug("Publish Alert to Topic: magic.alerts");
		
		//destination set in message-client-spring.xml
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTopicTemplate");
		
		jmsTemplate.send(new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                TextMessage message = null;
								
            	message = session.createTextMessage
						("Unauthorized use of magic supplies - Dice");
			
				message.setStringProperty("Mime Type", "text/html");
				message.setStringProperty("Snatcher", "Fenrir Greyback");
                
                return message;
            }

        });
		
		logger.debug("Check: http://localhost:8161/admin/topics.jsp");
	}
	
	/**
	 * Send a Bad Order to a Queue - should end up in DLQ
	 * @param context
	 */
	private static void sendBadOrder(ApplicationContext context) {
		
		logger.debug("Sending bad order: emagic.order");
		
		JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsQueueTemplate");
		
		jmsTemplate.send("emagic.orders", new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                TextMessage message = null;
					
            	message = session.createTextMessage
						("<malformed-order>2<malformed-order/>");
                
                return message;
            }

        });
		
		logger.debug("Check: http://localhost:8161/admin/queues.jsp");
	}
	
    /**
     * Helper method for retrieving test xml file
     * @param fileName
     * @return String
     */
    private static String getXmlFromFile(String fileName) throws Exception {
        String xmlInput;

        StringBuffer text = new StringBuffer();
        BufferedReader in = null;
        String line = null;
        
        in = new BufferedReader(new InputStreamReader(MessageClient.class
                .getResourceAsStream(fileName)));

        while ((line = in.readLine()) != null) {
            text.append(line);
        }

        xmlInput = text.toString();
        return xmlInput;
    }
}