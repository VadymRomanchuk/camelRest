package xyz.tomd.cameldemos.springboot.fileupload;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;


@Component
public class FileUploadRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .host("localhost").port("8082")
                .contextPath("/services")
                .apiContextPath("/api-doc")

                .enableCORS(true);

        rest("/rest")
                .get("/get").to("direct:helloGet")
                .post("/post").to("direct:helloPost");


        from("direct:helloGet")
                .setHeader("id", simple("${header.id}"))
                .setHeader(Exchange.CONTENT_TYPE, simple("text/plain"))
                .setBody(simple("Hello ${body}"))
                .to("http://b53e75c3-ec06-45e1-a83f-4916e68232b5.mock.pstmn.io/testGet?bridgeEndpoint=true")
                .process(exchange -> log.info("The response code is: {}",
                        exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE)))
                .setBody(simple("${body}"));

        from("direct:helloPost")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("id", simple("${header.id}"))
                .setHeader(Exchange.CONTENT_TYPE, simple("text/plain"))
                .setBody(simple("${body}"))
                .to("http://b53e75c3-ec06-45e1-a83f-4916e68232b5.mock.pstmn.io/testPost?bridgeEndpoint=true")
                .process(exchange -> log.info("The response code is: {}",
                        exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE)))
                .setBody(simple("${body}"));

    }
}
