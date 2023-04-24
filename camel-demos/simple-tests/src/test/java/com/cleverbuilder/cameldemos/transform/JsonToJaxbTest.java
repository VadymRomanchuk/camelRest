package com.cleverbuilder.cameldemos.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Reading in a JSON file and converting it to XML via JAXB classes,\
 * using a custom TypeConverter in Camel (you could also do this using
 * a Processor class)
 * Requires: camel-jaxb
 */
public class JsonToJaxbTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void transformJsonToJaxb() throws InterruptedException {
        mockOutput.expectedMessageCount(1);
        mockOutput.expectedBodiesReceived("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<album>\n" +
                "    <accolades>many</accolades>\n" +
                "    <artist>Cilla Black</artist>\n" +
                "    <rating>5.0</rating>\n" +
                "    <title>Surprise Surprise: The Very Best of Cilla Black, with additional remixes</title>\n" +
                "</album>\n");

        template.sendBody("direct:start",
                new File("src/test/data/album.json"));

        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
                JaxbDataFormat jaxb =
                        new JaxbDataFormat(JAXBContext.newInstance(Album.class));

                from("direct:start")
                        .unmarshal(json)
                        .log("Body unmarshalled, it is now ${body}")
                        .to("log:mylogger?showAll=true")
                        .wireTap("mock:intermediate-step")
                        .convertBodyTo(Album.class)
                        .marshal(jaxb)
                        .log("Body marshalled, it is now ${body}")
                        .to("mock:output");
            }
        };
    }

    /**
     * This would be your JAXB class, generated by CXF-XJC-Plugin, or similar.
     * We're just writing an ad-hoc one here, for demo purposes.
     */
    @XmlRootElement
    public static class Album {
        private String artist;
        private String title;
        private String rating;
        private String accolades;

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getAccolades() {
            return accolades;
        }

        public void setAccolades(String accolades) {
            this.accolades = accolades;
        }
    }
}
