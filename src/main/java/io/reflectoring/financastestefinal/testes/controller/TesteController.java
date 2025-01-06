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
    public void odds() {
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

    @GetMapping("/analise")
    public void google() {
        List<String> endpointsAvaliado = Arrays.asList("most-active", "gainers", "losers");
        Map<String, String> resultado = new HashMap<>();
        endpointsAvaliado.forEach(val -> {
            var client = HttpClient.newHttpClient();
            var requestMaisAtivos = HttpRequest.newBuilder(
                    URI.create("https://www.google.com/finance/markets/" + val))
                    .header("accept", "application/json")
                    .build();
            HttpResponse<String> responseMaisAtivos = null;
            try {
                responseMaisAtivos = client.send(requestMaisAtivos, BodyHandlers.ofString());
                String respostaMaisAtivos = responseMaisAtivos.body();
                List<String> maisAtivos = testeService.PegarConteudoPelaClasse(respostaMaisAtivos, "iLEcy");
                testeService.analiseTemporalMes(maisAtivos, val, resultado);

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("==========================================================ANALISE=================================================================");
        for (Map.Entry<String, String> entry : resultado.entrySet()) {
            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
        }


    }

    @GetMapping("/coin")
    public void analiseCoin() {
        List<String> endpointsAvaliado = new ArrayList<>();
        var client = HttpClient.newHttpClient();
        var requestMaisAtivos = HttpRequest.newBuilder(
                URI.create("https://www.google.com/finance/markets/cryptocurrencies"))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> responseMaisAtivos = null;
        try {
            responseMaisAtivos = client.send(requestMaisAtivos, BodyHandlers.ofString());
            String respostaMaisAtivos = responseMaisAtivos.body();
            List<String> criptos = testeService.PegarConteudoPelaClasse(respostaMaisAtivos, "iLEcy");
            List<String> finalEndpointsAvaliado = endpointsAvaliado;
            criptos.forEach(nm -> {
                String nome = "";
                if (!nm.contains(">")) {
                    nome = nm;
                } else {
                    nome = nm.split(">")[4];
                }
                finalEndpointsAvaliado.add(nome+"-USD");
            });
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//    endpointsAvaliado = Arrays.asList("ethereum", "ethena", "fusionist", "ether-fi-ethfi", "ankr", "pendle", "polygon", "omni", "pepe", "floki-inu", "saga", "polygon", "adventure-gold", "xrp", "render", "dogwifhat", "book-of-meme", "bitcoin", "dogecoin", "cardano", "shiba-inu", "polkadot", "near-protocol", "mantle", "stacks", "aptos", "aave-uni-v2", "internet-computer", "tron", "solana", "uniswap", "litecoin", "chainlink", "solana", "bitcoin-cash", "aave", "polkadot", "avalanche", "stellar", "arbitrum", "optimism", "standard-tokenization-protocol", "golem", "raydium", "audius", "worldcoin-org", "harvest-finance");
        Map<String, String> resultado = new HashMap<>();
        testeService.analiseCoin(endpointsAvaliado, resultado);
        System.out.println("==========================================================ANALISE=================================================================");
        for (Map.Entry<String, String> entry : resultado.entrySet()) {
            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
        }
//        Map<String, String> resultado = new HashMap<>();
//        endpointsAvaliado.forEach(val -> {
//            var client = HttpClient.newHttpClient();
//            var requestMaisAtivos = HttpRequest.newBuilder(
//                    URI.create("https://www.google.com/finance/markets/" + val))
//                    .header("accept", "application/json")
//                    .build();
//            HttpResponse<String> responseMaisAtivos = null;
//            try {
//                responseMaisAtivos = client.send(requestMaisAtivos, BodyHandlers.ofString());
//                String respostaMaisAtivos = responseMaisAtivos.body();
//                List<String> maisAtivos = testeService.PegarConteudoPelaClasse(respostaMaisAtivos, "iLEcy");
//                testeService.analiseTemporalMes(maisAtivos, val, resultado);
//
//            } catch (IOException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        System.out.println("==========================================================ANALISE=================================================================");
//        for (Map.Entry<String, String> entry : resultado.entrySet()) {
//            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
//        }


    }

    @GetMapping("/coin/{moedas}")
    public void analiseCoin(@PathVariable(value = "moedas") String moedas) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<String> endpointsAvaliado = Arrays.asList(moedas.split(","));
                Map<String, String> resultado = new HashMap<>();
                testeService.analiseCoin(endpointsAvaliado, resultado);
                System.out.println("==========================================================ANALISE=================================================================");
                for (Map.Entry<String, String> entry : resultado.entrySet()) {
                    System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
                }
            }
        };
        // Agende a tarefa para ser executada a cada minuto (60 segundos)
        timer.schedule(task, 0, 5 * 60 * 1000);

    }

    @GetMapping("/coinBase")
    public void analiseCoinCoinBase() {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List <String> endpointsAvaliado = Arrays.asList("ethereum", "bonk", "binaryx-new", "fusionist", "ether-fi-ethfi", "ankr", "pendle", "polygon", "omni", "pepe", "floki-inu", "saga", "polygon", "adventure-gold", "xrp", "render", "dogwifhat", "book-of-meme", "bitcoin", "dogecoin", "cardano", "shiba-inu", "polkadot", "near-protocol", "mantle", "stacks", "aptos", "aave-uni-v2", "internet-computer", "tron", "solana", "uniswap", "litecoin", "chainlink", "solana", "bitcoin-cash", "aave", "polkadot", "avalanche", "stellar", "arbitrum", "optimism", "standard-tokenization-protocol", "golem", "raydium", "audius", "worldcoin-org", "harvest-finance");
                Map<String, String> resultado = new HashMap<>();
                testeService.analiseCoinCoinbase(endpointsAvaliado, resultado, false);
            }
        };
        // Agende a tarefa para ser executada a cada 30 minutos
        timer.schedule(task, 0, 30 * 60 * 1000);

//        for (Map.Entry<String, String> entry : resultado.entrySet()) {
//            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
//        }
//        Map<String, String> resultado = new HashMap<>();
//        endpointsAvaliado.forEach(val -> {
//            var client = HttpClient.newHttpClient();
//            var requestMaisAtivos = HttpRequest.newBuilder(
//                    URI.create("https://www.google.com/finance/markets/" + val))
//                    .header("accept", "application/json")
//                    .build();
//            HttpResponse<String> responseMaisAtivos = null;
//            try {
//                responseMaisAtivos = client.send(requestMaisAtivos, BodyHandlers.ofString());
//                String respostaMaisAtivos = responseMaisAtivos.body();
//                List<String> maisAtivos = testeService.PegarConteudoPelaClasse(respostaMaisAtivos, "iLEcy");
//                testeService.analiseTemporalMes(maisAtivos, val, resultado);
//
//            } catch (IOException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        System.out.println("==========================================================ANALISE=================================================================");
//        for (Map.Entry<String, String> entry : resultado.entrySet()) {
//            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
//        }


    }

    @GetMapping("/coinBase/{moedas}")
    public void analiseCoinBase(@PathVariable(value = "moedas") String moedas) {
//        Timer timer = new Timer();
//
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                List<String> endpointsAvaliado = Arrays.asList(moedas.split(","));
//                Map<String, String> resultado = new HashMap<>();
//                testeService.analiseCoinCoinbase(endpointsAvaliado, resultado, true);
//                System.out.println("==========================================================ANALISE=================================================================");
//                for (Map.Entry<String, String> entry : resultado.entrySet()) {
//                    System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
//                }
//            }
//        };
//        // Agende a tarefa para ser executada a cada minuto (60 segundos)
//        timer.schedule(task, 0, 5 * 60 * 1000);

        List<String> endpointsAvaliado = Arrays.asList(moedas.split(","));
        Map<String, String> resultado = new HashMap<>();
        testeService.analiseCoinCoinbase(endpointsAvaliado, resultado, false);
//        System.out.println("==========================================================ANALISE=================================================================");
//        for (Map.Entry<String, String> entry : resultado.entrySet()) {
//            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
//        }

    }

//    @GetMapping("/monitoramento/{endpointAvaliado}/{acoes}")
//    public void googleMonitoramento(@PathVariable(value = "endpointAvaliado") String endpointAvaliado,
//                                    @PathVariable(value = "acoes") String acoes) {
//        List<String> acoesList = Arrays.asList(acoes.split(","));
//        LinkedHashMap<String, String> resultado = testeService.analiseTemporalMes(acoesList, true, endpointAvaliado);
//        System.out.println("==========================================================MON. MES=================================================================");
//        for (Map.Entry<String, String> entry : resultado.entrySet()) {
//            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
//        }
//
//    }

    @GetMapping("/monitoramentoDia/{acoes}")
    public void googleMonitoramentoDia(@PathVariable(value = "acoes") String acoes) {
        List<String> acoesList = Arrays.asList(acoes.split(","));
        LinkedHashMap<String, String> resultado = testeService.analiseTemporalDia(acoesList, false);
        System.out.println("==========================================================MON. DIA=================================================================");
        for (Map.Entry<String, String> entry : resultado.entrySet()) {
            System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
        }

    }

    @GetMapping("/monitoramentoGrafico/{acoes}")
    public void googleMonitoramentoGrafico(@PathVariable(value = "acoes") String acoes) {
        System.out.println(acoes);
        List<String> acoesList = Arrays.asList(acoes.split(","));
        long dataInicial = new Date().getTime();
        while ((new Date()).getTime() - dataInicial < 86400000) {
            if (((new Date()).getTime() - dataInicial) % (300000 / 5) == 0) {
                LinkedHashMap<String, String> resultado = testeService.analiseTemporal(acoesList, false);
                System.out.println("==========================================================MON. GRAFICO=================================================================");
                for (Map.Entry<String, String> entry : resultado.entrySet()) {
                    System.out.println("=> " + entry.getKey() + "=" + entry.getValue());
                }
            }
        }

    }

}
