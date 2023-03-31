package io.reflectoring.financastestefinal.testes.controller;

import io.reflectoring.financastestefinal.testes.service.TesteService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;

@Controller
@RequestMapping("/google")
public class TesteController {

    @Autowired
    private TesteService testeService;

    @GetMapping("/testeOdds")
    public void odds(){
        var client = HttpClient.newHttpClient();
        var requestMaisAtivos = HttpRequest.newBuilder(
                        URI.create("https://www.sportytrader.com/pt-br/odds/basquete/"))
                .header("accept", "application/html")
                .build();
        HttpResponse<String> responseMaisAtivos = null;
        try {
            responseMaisAtivos = client.send(requestMaisAtivos, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        testeService.processaOdds(responseMaisAtivos.body());

        String respostaMaisAtivos = responseMaisAtivos.body();
    }
    @GetMapping("/analise/{endpointAvaliado}/{mesDia}")
    public void google(@PathVariable(value = "endpointAvaliado") String endpointAvaliado,
                       @PathVariable(value = "mesDia") Boolean mesDia){
            var client = HttpClient.newHttpClient();
            var requestMaisAtivos = HttpRequest.newBuilder(
                            URI.create("https://www.google.com/finance/markets/"+endpointAvaliado))
                    .header("accept", "application/json")
                    .build();
            /*var requestMaiorGanho = HttpRequest.newBuilder(
                            URI.create("https://www.google.com/finance/markets/losers"))
                    .header("accept", "application/json")
                    .build();*/
            HttpResponse<String> responseMaisAtivos = null;
            /*HttpResponse<String> responseMaiorGanho = null;*/
            try {
                responseMaisAtivos = client.send(requestMaisAtivos, BodyHandlers.ofString());
                /*responseMaiorGanho = client.send(requestMaiorGanho, BodyHandlers.ofString());*/
                String respostaMaisAtivos = responseMaisAtivos.body();
                /*String respostaMaiorGanho = responseMaiorGanho.body();*/
                List<String> maisAtivos = testeService.PegarConteudoPelaClasse(respostaMaisAtivos, "iLEcy");
                /*List<String> maisGanhos =testeService.PegarConteudoPelaClasse(respostaMaiorGanho, "iLEcy");
                List<String> resultadoAnaliseInicial = testeService.comparar(maisAtivos, maisGanhos);*/
                LinkedHashMap<String, String> resultado = (mesDia ? testeService.analiseTemporalMes(maisAtivos, true) : testeService.analiseTemporalDia(maisAtivos, false));
                System.out.println("==========================================================ANALISE=================================================================");
                for (Map.Entry<String,String> entry : resultado.entrySet()) {
                    System.out.println("=> "+entry.getKey() + "=" +  entry.getValue());
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

    }
    @GetMapping("/monitoramento/{acoes}")
    public void googleMonitoramento(@PathVariable(value = "acoes") String acoes){
        List<String> acoesList = Arrays.stream(acoes.split(",")).toList();
        LinkedHashMap<String, String> resultado = testeService.analiseTemporalMes(acoesList, false);
        System.out.println("==========================================================MON. MES=================================================================");
        for (Map.Entry<String,String> entry : resultado.entrySet()) {
            System.out.println("=> "+entry.getKey() + "=" +  entry.getValue());
        }

    }

    @GetMapping("/monitoramentoDia/{acoes}")
    public void googleMonitoramentoDia(@PathVariable(value = "acoes") String acoes){
        List<String> acoesList = Arrays.stream(acoes.split(",")).toList();
        LinkedHashMap<String, String> resultado = testeService.analiseTemporalDia(acoesList, false);
        System.out.println("==========================================================MON. DIA=================================================================");
        for (Map.Entry<String,String> entry : resultado.entrySet()) {
            System.out.println("=> "+entry.getKey() + "=" +  entry.getValue());
        }

    }

    @GetMapping("/monitoramentoGrafico/{acoes}")
    public void googleMonitoramentoGrafico(@PathVariable(value = "acoes") String acoes){
        System.out.println(acoes);
        List<String> acoesList = Arrays.stream(acoes.split(",")).toList();
        long dataInicial = new Date().getTime();
        while((new Date()).getTime() - dataInicial < 86400000){
            if(((new Date()).getTime() - dataInicial) % (300000/5) == 0){
                LinkedHashMap<String, String> resultado = testeService.analiseTemporal(acoesList, false);
                System.out.println("==========================================================MON. GRAFICO=================================================================");
                for (Map.Entry<String,String> entry : resultado.entrySet()) {
                    System.out.println("=> "+entry.getKey() + "=" +  entry.getValue());
                }
            }
        }

    }

}
