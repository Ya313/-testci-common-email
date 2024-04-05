package org.apache.commons.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailTest {
	
	private static final String[] TEST_EMAILS = {"ab@bc.com","a.b@c.org", 
			"abcdefghijklmnopqrst@abcdefghijklmnopqrst.com.bd" };
	
	/* Concrete Email Class for testing */
	private EmailConcrete email;
	
	@Before
	public void setUpEmailTest() throws Exception {
	    email = new EmailConcrete();
        email.setHostName("smtp.example.com");
        email.setSmtpPort(465);
        email.setFrom("sender@example.com");
        email.setSubject("Test Subject");
        email.addTo(TEST_EMAILS);
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "localhost");
        email.setMailSession(Session.getDefaultInstance(properties));
	}
	
	@After
	public void tearDownEmailTest() throws Exception {
		

		
	}
	
	/*
	 * Test addBcc(String email) function
	 */
	@Test
	public void testAddBcc() throws Exception {
		
		email.addBcc(TEST_EMAILS);
		assertEquals(3, email.getBccAddresses().size());
		
	}
	
	/*
	 * Test addCC (String email) function
	 */
	@Test
	public void testAddCc() throws Exception {
	
		email.addCc(TEST_EMAILS);
		assertEquals(3, email.getCcAddresses().size());
	
	}
	
	/*
	 * Test addHeader(String name, String value) function
	 */
	@Test
	public void testAddHeaderValidity() throws Exception {
		// Verifies that a valid header and value successfully adds a header to the email 
        email.addHeader("X-HEADER", "testValue"); 
        assertEquals("testValue", email.getHeaders().get("X-HEADER"));
    }
	
    @Test(expected = IllegalArgumentException.class)
    // Test to see if a null name would gives an invalid response 
    public void testAddHeaderNullName() {
        email.addHeader(null, "testValue");
    }
    
    @Test(expected = IllegalArgumentException.class)
    // Test to insure that an empty name would gives an invalid response 
    public void testAddHeaderEmptyName() {
        email.addHeader("", "testValue");
    }
    
    @Test(expected = IllegalArgumentException.class)
    // Test to insure that a valid header and null value gives an invalid response
    public void testAddHeaderNullValue() {
        email.addHeader("X-HEADER", null);
    }
    
    /*
     * Test the addReplyTo(String email, String name) function
     */
    @Test
    public void testAddReplyTo() throws Exception {
        String replyToEmail = "reply-to@example.com";
        String replyToName = "Reply To";
        
        email.addReplyTo(replyToEmail, replyToName);

        assertEquals(1, email.getReplyToAddresses().size());
        InternetAddress replyToAddress = email.getReplyToAddresses().get(0);
        assertEquals(replyToEmail, replyToAddress.getAddress());
        assertEquals(replyToName, replyToAddress.getPersonal());
    }
    
    /*
     * Test the buildMimeMessage() function
     */
    @Test
    // Test that insures that buildmimemessage is working successfully
    public void testBuildMimeMessageSuccessfully() throws Exception {
        email.buildMimeMessage();
        MimeMessage message = email.getMimeMessage();
        assertNotNull(message); // Verify that the message is not null
        assertEquals("Test Subject", message.getSubject());
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildMimeMessageTwice() throws Exception {
        email.buildMimeMessage();
        // Insures that building again would throw an IllegalStateException
        email.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutFrom() throws Exception {
        EmailConcrete noFromEmail = new EmailConcrete();
        noFromEmail.setHostName("smtp.example.com");
        noFromEmail.setSmtpPort(465);
        // BuildMimeMessage should work successfully without using FROM
        noFromEmail.addTo(TEST_EMAILS);
        noFromEmail.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutRecipients() throws Exception {
        EmailConcrete noRecipientsEmail = new EmailConcrete();
        noRecipientsEmail.setHostName("smtp.example.com");
        noRecipientsEmail.setSmtpPort(465);
        noRecipientsEmail.setFrom("sender@example.com");
        // BuildMimeMessage should work successfully without including Recipients
        noRecipientsEmail.buildMimeMessage();
    }
    
    @Test
    // Insures that buildmimemessage works successfully with the addition of CC 
    public void testBuildMimeMessageWithCC() throws Exception {
        email.setFrom("test@example.com");
        email.addTo(TEST_EMAILS);
        email.addCc(TEST_EMAILS);
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        Address[] ccAddresses = message.getRecipients(Message.RecipientType.CC);
        assertNotNull(ccAddresses); // Verify that the ccAddress is not null
    }

    @Test
 // Insures that buildmimemessage works successfully with the addition of CC 
    public void testBuildMimeMessageWithBCC() throws Exception {
        email.setFrom("test@example.com");
        email.addTo(TEST_EMAILS);
        email.addBcc(TEST_EMAILS);
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        // This test expects that MimeMessage wouldn't throw an error when BCC is added
        assertTrue(true);
    }

    @Test
 // Insures that buildmimemessage works successfully with the addition of the ReplyToList
    public void testBuildMimeMessageWithReplyToList() throws Exception {
        email.setFrom("test@example.com");
        email.addTo(TEST_EMAILS);
        email.addReplyTo("replyto@example.com", "Reply To Name");
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        InternetAddress replyToAddress = (InternetAddress) message.getReplyTo()[0];
        assertEquals("replyto@example.com", replyToAddress.getAddress());
        assertEquals("Reply To Name", replyToAddress.getPersonal());
    }

    @Test
 // Insures that buildmimemessage works successfully with the addition of multiple headers
    public void testBuildMimeMessageWithHeaders() throws Exception {
        email.setFrom("test@example.com");
        email.addTo(TEST_EMAILS);
        email.addHeader("X-Header1", "Value1");
        email.addHeader("X-Header2", "Value2");
        email.buildMimeMessage();

        MimeMessage message = email.getMimeMessage();
        // verifies that multiple headers would successfully implement
        assertEquals("Value1", message.getHeader("X-Header1")[0]);
        assertEquals("Value2", message.getHeader("X-Header2")[0]);
    }
    
    /*
     * Test the getHostName() function
     */
    @Test
    public void testGetHostNameFromSession() throws EmailException {
        // Correct: Setting a single host name string instead of an array
        String expectedHostName = "smtp.example.com";

        // Session with mail host is created
        Properties props = new Properties();
        props.put(EmailConstants.MAIL_HOST, expectedHostName);
        Session session = email.getMailSession();

        // Ensure that the email's host name is retrieved from the session
        email.setMailSession(session);

        assertEquals(expectedHostName, email.getHostName());
    }

    @Test
    // Insures that the host name is successfully retrieved 
    public void testGetHostNameDirectly() {
        email.setHostName("smtp.example.com");
        assertEquals("smtp.example.com", email.getHostName());
    }

    @Test
    //Insures that the host name is initially set to null indicating that there is no information
    public void testGetHostNameWhenNotSet() {
        email.setHostName(null); // Clear any previously set hostNames
        assertNull(email.getHostName());
    }
    
    /*
     * Test the getMailSession() function
     */

    @Test
    //Insures that a new session is successfully created
    public void testGetMailSessionNewSession() throws EmailException {
        email.setHostName("smtp.example.com");
        Session session = email.getMailSession();
        assertNotNull(session); // Verify that the session is not null
        assertEquals("smtp.example.com", session.getProperty("mail.smtp.host"));
    }

    @Test(expected = EmailException.class)
    //Insures that GetMailSession throws an exception when there isn't a host name 
    public void testGetMailSessionWithoutHostname() throws EmailException {
        email.getMailSession();
    }
    @Test
    public void testGetMailSessionSSL_CheckServerIdentity() throws EmailException {
        email.setHostName("smtp.example.com");
        email.setStartTLSEnabled(true);// Insures that TLS is enabled
        email.setSSLOnConnect(true); // Insures that SSL is connected
        email.setSSLCheckServerIdentity(true); // Insures that the SSL server identity check is set
        Session session = email.getMailSession();
        assertNotNull(session);// Verify that the session is not null
        assertEquals("true", session.getProperty(EmailConstants.MAIL_SMTP_SSL_CHECKSERVERIDENTITY));
    }

    @Test
    //Test to insure that the MailSession work successfully with the addition of the BounceAddress 
    public void testGetMailSessionWithBounceAddress() throws EmailException {
        email.setHostName("smtp.example.com");
        email.setBounceAddress("bounce@example.com"); // Insures that the bounce address is set
        Session session = email.getMailSession();
        assertNotNull(session); // Verify that the session is not null
        assertEquals("bounce@example.com", session.getProperty("mail.smtp.from"));
    }
    
    /*
     * Test the getSentDate() function
     */
    
    @Test
    public void testGetSentDate() {
        // Set the sentDate to a specific time
        Date expectedSentDate = new Date(System.currentTimeMillis() - 100000); // 100 seconds ago
        email.setSentDate(expectedSentDate);
        
        Date actualSentDate = email.getSentDate();

        // Verifies that getSentDate() returns the date that was currently set
        assertEquals(expectedSentDate.getTime(), actualSentDate.getTime());
    }
    
    /*
     * Test the getSocketConnectionTimeout() function
     */
    
    @Test
    public void testGetSocketConnectionTimeout() {
        // Set the socket connection timeout
        int expectedTimeout = 5000; // Timeout value set in milliseconds
        email.setSocketConnectionTimeout(expectedTimeout); // Insures that the timeout is set

        int actualTimeout = email.getSocketConnectionTimeout();
        // Verifies that the actual timeout matches the expectedTimout that was initially set 
        assertEquals("The socket connection timeout should match the set value", expectedTimeout, actualTimeout);
    }
}
