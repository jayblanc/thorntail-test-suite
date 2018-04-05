package org.wildfly.swarm.ts.javaee.mail;

import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.store.StoredMessage;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.http.client.fluent.Request;
import org.awaitility.Awaitility;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MailTest {

    private static GreenMail greenMail;

    @BeforeClass
    public static void setUpMailServer() {
        ServerSetup serverSetup = new ServerSetup(3026, "127.0.0.1", ServerSetup.PROTOCOL_SMTP);
        greenMail = new GreenMail(serverSetup);
        greenMail.start();
    }

    @AfterClass
    public static void tearDownMailServer() {
        greenMail.stop();
    }

    @Before
    public void setup() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
    }

    @Test
    @RunAsClient
    public void testMail() throws IOException {
        String response = Request.Get("http://localhost:8080/mail").execute().returnContent().asString();
        assertThat(response).isEqualTo("OK");

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            GreenMailUser user = greenMail.setUser("swarm-test-suite@example.com", null);
            MailFolder inbox = greenMail.getManagers().getImapHostManager().getInbox(user);
            List<StoredMessage> messages = inbox.getMessages();
            assertThat(messages).isNotEmpty();
            MimeMultipart content = (MimeMultipart) messages.get(0).getMimeMessage().getContent();
            String body = content.getBodyPart(0).getContent().toString();
            assertThat(body).isEqualTo("FOO");
        });
    }

}
