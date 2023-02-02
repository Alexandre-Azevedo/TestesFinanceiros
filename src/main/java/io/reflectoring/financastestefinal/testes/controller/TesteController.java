package io.reflectoring.financastestefinal.testes.controller;

import ch.qos.logback.core.joran.spi.XMLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reflectoring.financastestefinal.testes.service.TesteService;
import org.apache.catalina.util.DOMWriter;
import org.apache.tomcat.util.descriptor.XmlIdentifiers;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;

@Controller
@RequestMapping("/teste")
public class TesteController {

    @Autowired
    private TesteService testeService;

    @GetMapping("/google")
    public void google(){
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(
                        URI.create("https://www.google.com/finance/markets/most-active"))
                .header("accept", "application/json")
                .build();
        /*var request = HttpRequest.newBuilder(
                        URI.create("https://www.google.com/finance/markets/gainers"))
                .header("accept", "application/json")
                .build();*/
        /*var request = HttpRequest.newBuilder(
                        URI.create("https://www.google.com/finance/markets/losers"))
                .header("accept", "application/json")
                .build();*/
        HttpResponse<String> response = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
            String resposta = response.body();
            testeService.PegarConteudoPelaClasse(resposta, "iLEcy");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
