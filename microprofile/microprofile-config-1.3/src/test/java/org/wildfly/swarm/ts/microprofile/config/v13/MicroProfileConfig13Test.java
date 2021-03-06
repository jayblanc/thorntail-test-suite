package org.wildfly.swarm.ts.microprofile.config.v13;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileConfig13Test {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo(""
                + "MY_ENVIRONMENT_VARIABLE: value from surefire envvar\n"
                + "MY.ENVIRONMENT.VARIABLE: value from surefire envvar\n"
                + "my.environment.variable: value from surefire envvar\n"
                + "MY_ENVIRONMENT_VARIABLE: value from surefire envvar\n"
                + "MY.ENVIRONMENT.VARIABLE: value from surefire envvar\n"
                + "my.environment.variable: value from surefire envvar\n"
                + "URI: http://microprofile.io\n"
                + "Optional notExisting: false\n"
                + "Optional existing: true\n"
                + "my.boolean.property: true\n"
                + "my.boolean.property: true\n"
                + "my.int.property: 42\n"
                + "my.int.property: 42\n"
                + "my.long.property: 9223372036854775807\n"
                + "my.long.property: 9223372036854775807\n"
                + "my.float.property: 3.14\n"
                + "my.float.property: 3.14\n"
                + "my.double.property: 10.0\n"
                + "my.double.property: 10.0\n"
                + "my.stringWrapper.property: Converted stringWrapperContent\n"
                + "my.stringWrapper.property: Converted stringWrapperContent\n"
                + "my.animals.array.property.array: Zebra, Alligator\n"
                + "my.animals.array.property.array: Zebra, Alligator\n"
                + "my.animals.array.property.list: [Zebra, Alligator]\n"
                + "my.animals.array.property.set: [Alligator, Zebra]\n"
        );
    }

    @Test
    @RunAsClient
    public void testEmptyProperty() throws IOException {
        // this behavior is not spec-compliant, but empty values treatment is disputed in MP Config
        String response = Request.Get("http://localhost:8080/empty-properties").execute().returnContent().asString();
        assertThat(response).isEqualTo("Empty system property: 'Property my.empty.system.property not found'\n"
                + "Empty property in config file: 'Property my.empty.property.in.config.file not found'\n");
    }
}
