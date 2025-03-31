package io.reflectoring.financastestefinal.testes.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

@Service
public class TesteService {
    private int contadorRecursao = 0;

    private static final String APPLICATION_NAME = "anytask-bpm";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public List<String> PegarConteudoPelaClasse(String html, String nomeClasse) {
        List<String> retorno = new ArrayList<>();

        String[] primeiraParte = html.split(nomeClasse);
        List<String> primeiraParteList = Arrays.asList(primeiraParte);
        List<String> primeiraParteListFormat = new ArrayList<>();

        for (int i = 0; i < primeiraParteList.size(); i++) {
            boolean validoOrInvalido = false;
            for (int j = 0; j < primeiraParteList.get(i).length(); j++) {
                if (primeiraParteList.get(i).charAt(j) == '>') {
                    validoOrInvalido = true;
                    break;
                }
            }
            if (validoOrInvalido) {
                primeiraParteListFormat.add(primeiraParteList.get(i));
            }
        }
        primeiraParteListFormat = primeiraParteListFormat.subList(2, primeiraParteListFormat.size());
        primeiraParteListFormat.forEach(x -> {
            int quantidadeAbrindo = 0;
            int quantidadeFechando = 0;
            String valorApendado = "";
            for (int i = 0; i < x.length(); i++) {
                if (x.charAt(i) == '>') {
                    quantidadeAbrindo++;

                }
                if (x.charAt(i) == '<') {
                    quantidadeFechando++;

                }
                if (quantidadeAbrindo != quantidadeFechando) {
                    valorApendado += x.charAt(i);
                }

            }
            retorno.add(valorApendado);
        });
        return retorno;
    }

    public List<String> PegarPercentual(String html, String nomeClasse) {
        List<String> retorno = new ArrayList<>();

        String[] primeiraParte = html.split("Diminuiu");
        List<String> primeiraParteList = Arrays.asList(primeiraParte);
        List<String> primeiraParteListFormat = new ArrayList<>();

        /*for(int i = 0; i < primeiraParteList.size(); i++){
            boolean validoOrInvalido = false;
            for(int j = 0; j < primeiraParteList.get(i).length(); j++){
                if(primeiraParteList.get(i).charAt(j) == '>'){
                    validoOrInvalido = true;
                    break;
                }
            }
            if(validoOrInvalido){
                primeiraParteListFormat.add(primeiraParteList.get(i));
            }
        }
        primeiraParteListFormat = primeiraParteListFormat.subList(2, primeiraParteListFormat.size());
        primeiraParteListFormat.forEach(x -> {
            int quantidadeAbrindo = 0;
            int quantidadeFechando = 0;
            String valorApendado = "";
            for (int i = 0; i < x.length(); i++) {
                if(x.charAt(i) == '>'){
                    quantidadeAbrindo++;

                }
                if(x.charAt(i) == '<'){
                    quantidadeFechando++;

                }
                if(quantidadeAbrindo != quantidadeFechando){
                    valorApendado += x.charAt(i);
                }

            }
            retorno.add(valorApendado);
        });*/
        return retorno;
    }

    public LinkedHashMap<String, String> analiseTemporalDia(List<String> nomesAcoes, boolean analiseInicial) {
        LinkedHashMap<String, String> variacaoMedia = new LinkedHashMap<>();
        nomesAcoes.forEach(nm -> {
            String nomeAcao = "";
            if (!nm.contains(">")) {
                nomeAcao = nm;
            } else {
                nomeAcao = nm.split(">")[4];
            }
            var client = HttpClient.newHttpClient();
            var requestMaisAtivos = HttpRequest.newBuilder(
                    URI.create("https://www.google.com/finance/quote/" + nomeAcao + ":BVMF?window=5D"))
                    .header("accept", "application/json")
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(requestMaisAtivos, HttpResponse.BodyHandlers.ofString());
                String resposta = response.body();
                String[] primeiraParte = resposta.split("key: 'ds:10'");
                String[] segundaParte = primeiraParte[1].split("sideChannel");
                String terceiraParte = segundaParte[0].substring(60, segundaParte[0].length() - 2);
                String[] quartaParte = terceiraParte.split(",");

                String[] primeiraParteMes = resposta.split("key: 'ds:11'");
                String[] segundaParteMes = primeiraParte[1].split("sideChannel");
                String terceiraParteMes = segundaParte[0].substring(60, segundaParte[0].length() - 2);
                String[] quartaParteMes = terceiraParte.split(",");

                List<Double> valores = new ArrayList<>();
                List<String> horas = new ArrayList<>();
                List<String> data = new ArrayList<>();
                List<Double> valoresVolume = new ArrayList<>();
                for (int i = quartaParte[25].substring(1).equals("-10800]]") ? 26 : 25; i < quartaParte.length; i += 15) {
                    valores.add(Double.valueOf(quartaParte[i].substring(1)));
                    if (quartaParte[i + 6].contains("]]")) {
                        valoresVolume.add(Double.valueOf(quartaParte[i + 6].substring(0, quartaParte[i + 6].length() - 2)));
                        horas.add(quartaParte[i - 5] + ":" + quartaParte[i - 4]);
                        data.add(quartaParte[i - 6] + "/" + quartaParte[i - 7]);
                    } else {
                        valoresVolume.add(Double.valueOf(quartaParte[i + 6].substring(0, quartaParte[i + 6].length() - 1)));
                        horas.add(quartaParte[i - 5] + ":" + quartaParte[i - 4]);
                        data.add(quartaParte[i - 6] + "/" + quartaParte[i - 7]);
                    }
                }


                List<Double> variacao = new ArrayList<>();
                AtomicReference<Double> aux = new AtomicReference<>(null);
                valores.stream().forEach(x -> {
                    if (aux.get() != null) {
                        variacao.add(((x / aux.get()) - 1) * 100);
                    }
                    aux.set(x);
                });
                List<Double> variacaoVolume = new ArrayList<>();
                AtomicReference<Double> auxVolume = new AtomicReference<>(null);
                valoresVolume.stream().forEach(x -> {
                    if (auxVolume.get() != null && auxVolume.get() != 0) {
                        variacaoVolume.add(((x / auxVolume.get()) - 1) * 100);
                    }
                    auxVolume.set(x);
                });
                Double valorModa = modaValue(valores).doubleValue();
                Double valorMedia = Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()).doubleValue();
                Double valorMediaVolume = Double.valueOf(variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolume.size()).doubleValue();
                Double varianciaValores = variancia(variacao, valorMedia);
                Double varianciaVolume = variancia(variacaoVolume, valorMediaVolume);
                Double covariancia = covariancia(variacao, variacaoVolume, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                /*Double isNormal = isNormal(valores, variancia, valorMedia);*/

                if (!analiseInicial) {
                    /*if((valorModa < valores.get(valores.size() - 1) && valores.get(valores.size() - 1) < valorMedia) ||
                        (valorModa > valores.get(valores.size() - 1) && valores.get(valores.size() - 1) > valorMedia)){
                        System.out.println(nomeAcao+" - possibilidade de venda");
                    }*/
                    variacaoMedia.put(nomeAcao, String.format("%.2f", Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " | " +
                            String.format("%.2f", variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / valoresVolume.size()) + " | " +
                            "5D covariancia: " + String.format("%.4f", covariancia) + " | " +
                            "5D " + moda(valores) + " | " +
                            "5D Media: " + String.format("%.2f", (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size()) + " | " +
                            "5D Atual: " + valores.get(valores.size() - 1).toString() + "\n" +
                            horas.get(horas.size() - 1) + " | " + headShoulderPattern(valores) + " | " +
                            headShoulderReversePattern(valores) + " | " +
                            cunhaDeBaixaPattern(valores) + " | " +
                            trinaguloDeReversaoPattern(valores) + " | " +
                            cestoBasePattern(valores, variacaoVolume) + "\n" +

                            (variacao.size() > 27 ?
                                    predicaoPolinomialLagrange(variacao.subList(variacao.size() - 27, variacao.size()), variacaoVolume.subList(variacaoVolume.size() - 27, variacaoVolume.size())) :
                                    predicaoPolinomialLagrange(variacao, variacaoVolume)) + " | " +
                            (variacao.size() > 27 ?
                                    predicaoPolinomialNewton(variacao.subList(variacao.size() - 27, variacao.size()), variacaoVolume.subList(variacaoVolume.size() - 27, variacaoVolume.size())) :
                                    predicaoPolinomialNewton(variacao, variacaoVolume)));
                }

                Double menorValorMediaModa = modaValue(valores).doubleValue() <= Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue() ?
                        modaValue(valores).doubleValue() : Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue();

                if (valores.get(valores.size() - 1).doubleValue() < menorValorMediaModa) {
                    if (analiseInicial) {
                        variacaoMedia.put(nomeAcao, String.format("%.2f", Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " | " +
                                String.format("%.2f", variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / valoresVolume.size()) + " | " +
                                "5D covariancia: " + String.format("%.4f", covariancia) + " | " +
                                "5D " + moda(valores) + " | " +
                                "5D Media: " + String.format("%.2f", (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size()) + " | " +
                                "5D Atual: " + valores.get(valores.size() - 1).toString() + "\n" +
                                horas.get(horas.size() - 1) + " | " + headShoulderPattern(valores) + " | " +
                                headShoulderReversePattern(valores) + " | " +
                                cunhaDeBaixaPattern(valores) + " | " +
                                trinaguloDeReversaoPattern(valores) + " | " +
                                cestoBasePattern(valores, variacaoVolume) + "\n" +

                                predicaoPolinomialLagrange(variacao, variacaoVolume) + " | " +
                                predicaoPolinomialNewton(variacao, variacaoVolume));
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return variacaoMedia;
    }

    public LinkedHashMap<String, String> analiseTemporal(List<String> nomesAcoes, boolean analiseInicial) {
        LinkedHashMap<String, String> variacaoMedia = new LinkedHashMap<>();
        nomesAcoes.forEach(nm -> {
            String nomeAcao = "";
            if (!nm.contains(">")) {
                nomeAcao = nm;
            } else {
                nomeAcao = nm.split(">")[4];
            }
            var client = HttpClient.newHttpClient();
            var requestMaisAtivos = HttpRequest.newBuilder(
                    URI.create("https://www.google.com/finance/quote/" + nomeAcao + ":BVMF?window=5D"))
                    .header("accept", "application/json")
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(requestMaisAtivos, HttpResponse.BodyHandlers.ofString());
                String resposta = response.body();
                String[] primeiraParte = resposta.split("key: 'ds:10'");
                String[] segundaParte = primeiraParte[1].split("sideChannel");
                String terceiraParte = segundaParte[0].substring(60, segundaParte[0].length() - 2);
                String[] quartaParte = terceiraParte.split(",");

                List<Double> valores = new ArrayList<>();
                List<String> horas = new ArrayList<>();
                List<String> data = new ArrayList<>();
                List<Double> valoresVolume = new ArrayList<>();
                for (int i = quartaParte[25].substring(1).equals("-10800]]") ? 26 : 25; i < quartaParte.length; i += 15) {
                    valores.add(Double.valueOf(quartaParte[i].substring(1)));
                    if (quartaParte[i + 6].contains("]]")) {
                        valoresVolume.add(Double.valueOf(quartaParte[i + 6].substring(0, quartaParte[i + 6].length() - 2)));
                        horas.add(quartaParte[i - 5] + ":" + quartaParte[i - 4]);
                        data.add(quartaParte[i - 6] + "/" + quartaParte[i - 7]);
                    } else {
                        valoresVolume.add(Double.valueOf(quartaParte[i + 6].substring(0, quartaParte[i + 6].length() - 1)));
                        horas.add(quartaParte[i - 5] + ":" + quartaParte[i - 4]);
                        data.add(quartaParte[i - 6] + "/" + quartaParte[i - 7]);
                    }
                }


                List<Double> variacao = new ArrayList<>();
                AtomicReference<Double> aux = new AtomicReference<>(null);
                valores.stream().forEach(x -> {
                    if (aux.get() != null) {
                        variacao.add(((x / aux.get()) - 1) * 100);
                    }
                    aux.set(x);
                });
                List<Double> variacaoVolume = new ArrayList<>();
                AtomicReference<Double> auxVolume = new AtomicReference<>(null);
                valoresVolume.stream().forEach(x -> {
                    if (auxVolume.get() != null && auxVolume.get() != 0) {
                        variacaoVolume.add(((x / auxVolume.get()) - 1) * 100);
                    }
                    auxVolume.set(Double.valueOf(x));
                });
                Double valorModa = modaValue(valores).doubleValue();
                Double valorMedia = Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()).doubleValue();
                Double valorMediaVolume = Double.valueOf(variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolume.size()).doubleValue();
                Double varianciaValores = variancia(variacao, valorMedia);
                Double varianciaVolume = variancia(variacaoVolume, valorMediaVolume);
                Double covariancia = covariancia(variacao, variacaoVolume, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                /*Double isNormal = isNormal(valores, variancia, valorMedia);*/

                if (!analiseInicial) {
                    /*if((valorModa < valores.get(valores.size() - 1) && valores.get(valores.size() - 1) < valorMedia) ||
                        (valorModa > valores.get(valores.size() - 1) && valores.get(valores.size() - 1) > valorMedia)){
                        System.out.println(nomeAcao+" - possibilidade de venda");
                    }*/
                    variacaoMedia.put(nomeAcao, horas.get(horas.size() - 1) + " | " +
                            "5D Atual: " + valores.get(valores.size() - 1).toString() + " | " +
                            headShoulderPattern(valores) + " | " +
                            headShoulderReversePattern(valores) + " | " +
                            cunhaDeBaixaPattern(valores) + " | " +
                            trinaguloDeReversaoPattern(valores) + " | " +
                            cestoBasePattern(valores, variacaoVolume) + "\n" +

                            predicaoPolinomialLagrange(variacao, variacaoVolume) + " | " +
                            predicaoPolinomialNewton(variacao, variacaoVolume));
                }

                Double menorValorMediaModa = modaValue(valores).doubleValue() <= Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue() ?
                        modaValue(valores).doubleValue() : Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue();

                if (valores.get(valores.size() - 1).doubleValue() < menorValorMediaModa) {
                    if (analiseInicial) {
                        variacaoMedia.put(nomeAcao, horas.get(horas.size() - 1) + " | " +
                                "5D Atual: " + valores.get(valores.size() - 1).toString() + " | " +
                                headShoulderPattern(valores) + " | " +
                                headShoulderReversePattern(valores) + " | " +
                                cunhaDeBaixaPattern(valores) + " | " +
                                trinaguloDeReversaoPattern(valores) + " | " +
                                cestoBasePattern(valores, variacaoVolume) + "\n" +

                                predicaoPolinomialLagrange(variacao, variacaoVolume) + " | " +
                                predicaoPolinomialNewton(variacao, variacaoVolume));
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return variacaoMedia;
    }

    public void analiseTemporalMes(List<String> nomesAcoes, String endpointAvaliado, Map<String, String> mapResultado) {
        nomesAcoes.forEach(nm -> {
            String nomeAcao = "";
            if (!nm.contains(">")) {
                nomeAcao = nm;
            } else {
                nomeAcao = nm.split(">")[4];
            }
            var client = HttpClient.newHttpClient();
//            WebDriverManager.chromedriver().setup();
            Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
            System.setProperty("webdriver.chrome.silentOutput", "true");
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments(new String[]{"--remote-allow-origins=*"});
            chromeOptions.addArguments(new String[]{"test-type"});
            chromeOptions.addArguments(new String[]{"start-maximized"});
            chromeOptions.addArguments(new String[]{"disable-web-security"});
            chromeOptions.addArguments(new String[]{"allow-running-insecure-content"});
            chromeOptions.addArguments(new String[]{"--log-level=3"});
            chromeOptions.addArguments(new String[]{"--ignore-ssl-errors=yes"});
            chromeOptions.addArguments(new String[]{"--ignore-certificate-errors"});
            chromeOptions.addArguments(new String[]{"--kiosk-printing"});
            chromeOptions.addArguments(new String[]{"--lang=pt-BR"});
            chromeOptions.addArguments(new String[]{"--disable-gpu"});
            chromeOptions.addArguments(new String[]{"--disable-dev-shm-usage"});
            chromeOptions.addArguments(new String[]{"--no-sandbox"});
            chromeOptions.addArguments(new String[]{"--disable-session-crashed-bubble"});
            chromeOptions.addArguments(new String[]{"--no-sandbox"});
            chromeOptions.addArguments(new String[]{"--disable-dev-shm-usage"});
            chromeOptions.addArguments(new String[]{"--disable-blink-features=AutomationControlled"});
            chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
            chromeOptions.setExperimentalOption("useAutomationExtension", (Object) null);
            chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.addArguments("--headless");
            WebDriver chromeWebDriver = new ChromeDriver(chromeOptions);
            try {
                chromeWebDriver.get("https://www.google.com/finance/quote/" + (!endpointAvaliado.equals("cryptocurrencies") ? nomeAcao + ":BVMF" : nomeAcao + "-USD") + "?window=1M");
                List<Double> valores = new ArrayList<>();
                List<Double> variacaoVolume = new ArrayList<>();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String regex = ".*[a-zA-Z].*";
                if (!chromeWebDriver.findElement(By.tagName("body")).getText().contains("Não há dados")
                        && !chromeWebDriver.findElement(By.tagName("body")).getText().contains("Não encontramos resultados para sua pesquisa.")) {
                    WebElement element = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(10)).until(
                            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + (!endpointAvaliado.equals("cryptocurrencies") ? nomeAcao + ":BVMF" : nomeAcao + "-USD") + "?window=1M'] path[fill='none']"))
                    );
                    List<String> input = Arrays.stream(element.getAttribute("d").split("L")).map(val -> val.replace("M", "")).collect(Collectors.toList());
                    List<Double> finalVariacaoVolume = variacaoVolume;
                    List<Double> finalValores = valores;
                    input.forEach(val -> {
                        String[] valSpliter = val.split(",");
                        finalValores.add(Double.valueOf(valSpliter[0]));
                        finalVariacaoVolume.add(Double.valueOf(valSpliter[1]));
                    });
                    Collections.reverse(variacaoVolume);
                    Double valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue();
                    Double valorMediaVolume = Double.valueOf(variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolume.size()).doubleValue();
                    Double varianciaValores = variancia(valores, valorMedia);
                    Double varianciaVolume = variancia(variacaoVolume, valorMediaVolume);
                    Double covariancia = covariancia(valores, variacaoVolume, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                    Double lagrangeValor = predicaoPolinomialLagrange(valores, variacaoVolume);
                    if (lagrangeValor > 0 && covariancia > 0) {
                        String pattern = "R\\$\\d+\\.\\d+"; // Padrão regex para capturar o valor
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(chromeWebDriver.findElement(By.tagName("body")).getText());
                        Double value = null;
                        if (m.find()) {
                            String valueStr = m.group().replace("R$", "").trim(); // Captura o valor como uma string
                            value = Double.parseDouble(valueStr.replace(",", ".")); // Converte a string para double
                        }
                        Double variacaoEstimada = null;
                        if (value != null) {
                            variacaoEstimada = -0.0020639 * Math.pow(value, 2.0) + 0.1023252 * value + 0.1423548;
                        } else {
                            variacaoEstimada = 0.0;
                        }
                        mapResultado.put(nomeAcao, "1M covariancia: " + String.format("%.4f", covariancia) + " | 1M " +
                                "PREDICAO POLINOMIAL LAGRANGE - ( " + lagrangeValor + " )" + " | 1M " +
                                "PREDICAO POLINOMIAL NEWTON - ( " + predicaoPolinomialNewton(valores, variacaoVolume) + " ) | Variação Estimada % - ( " + variacaoEstimada + " ) | Valor de Venda Estimada - ( " + value * (1 + (variacaoEstimada / 100)) + " ) ");


                        valores = new ArrayList<>();
                        variacaoVolume = new ArrayList<>();
                        chromeWebDriver.get("https://www.google.com/finance/quote/" + (!endpointAvaliado.equals("cryptocurrencies") ? nomeAcao + ":BVMF" : nomeAcao + "-USD") + "?window=5D");
                        valores = new ArrayList<>();
                        variacaoVolume = new ArrayList<>();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (!chromeWebDriver.findElement(By.tagName("body")).getText().contains("Não há dados")
                                    && !chromeWebDriver.findElement(By.tagName("body")).getText().contains("Não encontramos resultados para sua pesquisa.")) {
                                element = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(10)).until(
                                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + (!endpointAvaliado.equals("cryptocurrencies") ? nomeAcao + ":BVMF" : nomeAcao + "-USD") + "?window=5D'] path[fill='none']"))
                                );
                                input = Arrays.stream(element.getAttribute("d").split("L")).map(val -> val.replace("M", "")).collect(Collectors.toList());
                                List<Double> finalValores1 = valores;
                                List<Double> finalVariacaoVolume1 = variacaoVolume;
                                input.forEach(val -> {
                                    String[] valSpliter = val.split(",");
                                    finalValores1.add(Double.valueOf(valSpliter[0]));
                                    finalVariacaoVolume1.add(Double.valueOf(valSpliter[1]));
                                });

                                valores = valores.subList(valores.size() - 31, valores.size());
                                Collections.reverse(variacaoVolume);
                                variacaoVolume = variacaoVolume.subList(variacaoVolume.size() - 31, variacaoVolume.size());
                                valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue();
                                valorMediaVolume = Double.valueOf(variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolume.size()).doubleValue();
                                varianciaValores = variancia(valores, valorMedia);
                                varianciaVolume = variancia(variacaoVolume, valorMediaVolume);
                                covariancia = covariancia(valores, variacaoVolume, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                                lagrangeValor = predicaoPolinomialLagrange(valores, variacaoVolume);
                                if (lagrangeValor > 0 && covariancia > 0) {
                                    mapResultado.put(nomeAcao, mapResultado.get(nomeAcao) + " | 5D covariancia: " + String.format("%.4f", covariancia) + " | 5D " +
                                            "PREDICAO POLINOMIAL LAGRANGE - ( " + predicaoPolinomialLagrange(valores, variacaoVolume) + " ) | 5D " +
                                            "PREDICAO POLINOMIAL NEWTON - ( " + predicaoPolinomialNewton(valores, variacaoVolume) + " ) ");
                                } else {
                                    mapResultado.remove(nomeAcao);
                                }
                            }
                        } catch (Exception ex) {
                            System.out.printf(nomeAcao);
                            ex.printStackTrace();
                        }
                    }

                }
            } catch (Exception ex) {
                System.out.printf(nomeAcao);
                ex.printStackTrace();
            }
            chromeWebDriver.close();
        });
    }

    public void analiseCoin(List<String> nomesMoedas, Map<String, String> mapResultado) {
        nomesMoedas.forEach(nm -> {
            Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
            System.setProperty("webdriver.chrome.silentOutput", "true");

            ChromeOptions chromeOptions = chromeOptionsSemHeadLess();
//            ChromeOptions chromeOptionsToProxy = chromeOptionsComHeadLess();
//
//            List<Map<String,Object>> proxies = getFreeProxies(new ChromeDriver(chromeOptionsToProxy));
//
//            Random random = new Random();
//            int randomNumber = random.nextInt(proxies.size());
//
//            Proxy proxy = new Proxy();
//            proxy.setHttpProxy(proxies.get(randomNumber).get("IP Address").toString() + ":" + proxies.get(randomNumber).get("Port").toString());
//            proxy.setSslProxy(proxies.get(randomNumber).get("IP Address").toString() + ":" + proxies.get(randomNumber).get("Port").toString());
//            chromeOptions.setProxy(proxy);
            WebDriver chromeWebDriver = new ChromeDriver(chromeOptions);
            try {
                chromeWebDriver.get("https://www.google.com/finance/quote/" + nm);
                List<Double> valores = new ArrayList<>();
                List<Double> variacaoVolume = new ArrayList<>();
                WebDriverWait wait = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(20));
//                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form:nth-child(1) > div > div > button"))).click();
//                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + nm + ":BVMF" + "?window=1M'] path[fill='none']"))

                List<WebElement> elements = null;
                String pontos = null;
                try {
                    elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + nm + "'] path")));
                    pontos = elements.get(elements.size() - 1).getAttribute("d");
                } catch (Exception ex) {
                    elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + nm + "'] path")));
                    pontos = elements.get(elements.size() - 1).getAttribute("d");
                }
//                List<String> input = Arrays.stream(element.getAttribute("d").split("L")).map(val -> val.replace("M", "")).collect(Collectors.toList());
                List<String> input = new ArrayList<>();
                Pattern pattern = Pattern.compile("-?\\d+\\.\\d+");
                Matcher matcher = pattern.matcher(pontos);

                while (matcher.find()) {
                    input.add(matcher.group());
                }
                List<Double> finalVariacaoVolume = variacaoVolume;
                List<Double> finalValores = valores;
                List<Double> finalValores2 = finalValores;
                List<Double> finalVariacaoVolume2 = finalVariacaoVolume;
                List<String> finalInput = input;
                input.forEach(val -> {
                    if (finalInput.indexOf(val) % 2 != 0) {
                        finalValores2.add(Double.valueOf(val));
                    } else {
                        finalVariacaoVolume2.add(Double.valueOf(val));
                    }
//                    String[] valSpliter = val.split(",");
//                    finalValores2.add(Double.valueOf(valSpliter[0]));
//                    finalVariacaoVolume2.add(Double.valueOf(valSpliter[1]));
                });
                Collections.reverse(variacaoVolume);
                List<Double> valoresMedia = new ArrayList<>();
                List<Double> variacaoVolumeMedia = new ArrayList<>();
                if (valores.size() != variacaoVolume.size()) {
                    valores = valores.subList(0, valores.size() / 2);
                }
                for (int i = 0; i < valores.size(); i += Double.valueOf(valores.size() / 24).intValue()) {
                    List<Double> valoresAux = new ArrayList<>();
                    List<Double> variacaoVolumeAux = new ArrayList<>();
                    if (valores.size() >= i + Double.valueOf(valores.size() / 24).intValue()) {
                        valoresAux = valores.subList(i, i + Double.valueOf(valores.size() / 24).intValue());
                        variacaoVolumeAux = variacaoVolume.subList(i, i + Double.valueOf(variacaoVolume.size() / 24).intValue());
                    } else {
                        valoresAux = valores.subList(i, valores.size());
                        variacaoVolumeAux = variacaoVolume.subList(i, variacaoVolume.size());
                    }
                    valoresMedia.add(valoresAux.stream().mapToDouble(Double::doubleValue).sum() / valoresAux.size());
                    variacaoVolumeMedia.add(variacaoVolumeAux.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolumeAux.size());
                }
                if (valoresMedia.size() > 25) {
                    valoresMedia = valoresMedia.subList(valoresMedia.size() - 25, valoresMedia.size());
                }

                if (variacaoVolumeMedia.size() > 25) {
                    variacaoVolumeMedia = variacaoVolumeMedia.subList(variacaoVolumeMedia.size() - 25, variacaoVolumeMedia.size());
                }

                Double valorMedia = Double.valueOf(valoresMedia.stream().mapToDouble(Double::doubleValue).sum() / valoresMedia.size()).doubleValue();
                Double valorMediaVolume = Double.valueOf(variacaoVolumeMedia.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolumeMedia.size()).doubleValue();
                Double varianciaValores = variancia(valoresMedia, valorMedia);
                Double varianciaVolume = variancia(variacaoVolumeMedia, valorMediaVolume);
                Double covariancia = covariancia(valoresMedia, variacaoVolumeMedia, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                Double lagrangeValor = predicaoPolinomialLagrange(valoresMedia, variacaoVolumeMedia);
                mapResultado.put(nm, "1D covariancia: " + String.format("%.4f", covariancia) + " | ABAIXO MÉDIA 1D -( " + String.valueOf(valorMediaVolume > variacaoVolume.get(variacaoVolume.size() - 1)) + " )" + " | 1D " +
                        "PREDICAO POLINOMIAL LAGRANGE - ( " + lagrangeValor + " )" + " | 1D " +
                        "PREDICAO POLINOMIAL NEWTON - ( " + predicaoPolinomialNewton(valoresMedia, variacaoVolumeMedia) + " )");

//                wait = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(20));
//                WebElement elementClickMes = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='5dayTab']")));
//                elementClickMes.click();

                chromeWebDriver.get("https://www.google.com/finance/quote/" + nm + "?window=5D");

                wait = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(20));
                WebElement element = null;
                try {
                    element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + nm + "'] path[fill='none']")));
                    pontos = element.getAttribute("d");
                } catch (Exception ex) {
                    element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + nm + "'] path[fill='none']")));
                    pontos = element.getAttribute("d");
                }
//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[mask*='url(https://www.google.com/finance/quote/" + nm + "'] path[fill='none']")));
//                pontos = element.getAttribute("d");
//                List<String> input = Arrays.stream(element.getAttribute("d").split("L")).map(val -> val.replace("M", "")).collect(Collectors.toList());
                input = new ArrayList<>();
                Pattern patternDois = Pattern.compile("-?\\d+\\.\\d+");
                Matcher matcherDois = patternDois.matcher(pontos);

                while (matcherDois.find()) {
                    input.add(matcherDois.group());
                }
                finalVariacaoVolume = variacaoVolume;
                finalValores = valores;
                List<Double> finalValores1 = finalValores;
                List<Double> finalVariacaoVolume1 = finalVariacaoVolume;
                List<String> finalInput1 = input;
                input.forEach(val -> {
                    if (finalInput1.indexOf(val) % 2 != 0) {
                        finalValores1.add(Double.valueOf(val));
                    } else {
                        finalVariacaoVolume1.add(Double.valueOf(val));
                    }
//                    String[] valSpliter = val.split(",");
//                    finalValores1.add(Double.valueOf(valSpliter[0]));
//                    finalVariacaoVolume1.add(Double.valueOf(valSpliter[1]));
                });
                Collections.reverse(variacaoVolume);
                valoresMedia = new ArrayList<>();
                variacaoVolumeMedia = new ArrayList<>();
                if (valores.size() != variacaoVolume.size()) {
                    valores = valores.subList(0, valores.size() / 2);
                }
//                valores = valores.subList(0, valores.size()/2);
                for (int i = 0; i < valores.size(); i += Double.valueOf(valores.size() / 24).intValue()) {
                    List<Double> valoresAux = new ArrayList<>();
                    List<Double> variacaoVolumeAux = new ArrayList<>();
                    if (valores.size() >= i + Double.valueOf(valores.size() / 24).intValue()) {
                        valoresAux = valores.subList(i, i + Double.valueOf(valores.size() / 24).intValue());
                        variacaoVolumeAux = variacaoVolume.subList(i, i + Double.valueOf(variacaoVolume.size() / 24).intValue());
                    } else {
                        valoresAux = valores.subList(i, valores.size());
                        variacaoVolumeAux = variacaoVolume.subList(i, variacaoVolume.size());
                    }
                    valoresMedia.add(valoresAux.stream().mapToDouble(Double::doubleValue).sum() / valoresAux.size());
                    variacaoVolumeMedia.add(variacaoVolumeAux.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolumeAux.size());
                }

                if (valoresMedia.size() > 25) {
                    valoresMedia = valoresMedia.subList(valoresMedia.size() - 25, valoresMedia.size());
                }

                if (variacaoVolumeMedia.size() > 25) {
                    variacaoVolumeMedia = variacaoVolumeMedia.subList(variacaoVolumeMedia.size() - 25, variacaoVolumeMedia.size());
                }

                valorMedia = Double.valueOf(valoresMedia.stream().mapToDouble(Double::doubleValue).sum() / valoresMedia.size()).doubleValue();
                valorMediaVolume = Double.valueOf(variacaoVolumeMedia.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolumeMedia.size()).doubleValue();
                varianciaValores = variancia(valoresMedia, valorMedia);
                varianciaVolume = variancia(variacaoVolumeMedia, valorMediaVolume);
                covariancia = covariancia(valoresMedia, variacaoVolumeMedia, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                lagrangeValor = predicaoPolinomialLagrange(valoresMedia, variacaoVolumeMedia);
                mapResultado.put(nm, mapResultado.get(nm) + " - 1S covariancia: " + String.format("%.4f", covariancia) + " | ABAIXO MÉDIA 1S -( " + String.valueOf(valorMediaVolume > variacaoVolume.get(variacaoVolume.size() - 1)) + " )" + " | 1S " +
                        "PREDICAO POLINOMIAL LAGRANGE - ( " + lagrangeValor + " )" + " | 1S " +
                        "PREDICAO POLINOMIAL NEWTON - ( " + predicaoPolinomialNewton(valoresMedia, variacaoVolumeMedia) + " )");
                chromeWebDriver.close();
            } catch (Exception e) {
                chromeWebDriver.close();
            }
        });
    }

    public void analiseCoinCoinbase(List<String> nomesMoedas, Map<String, String> mapResultado, Boolean usarProxy, Boolean filtrarDados) {
        ChromeOptions chromeOptionsToProxy = null;
        List<Map<String, Object>> proxies = null;
        if (usarProxy) {
            chromeOptionsToProxy = chromeOptionsComHeadLess();

            proxies = getFreeProxies(new ChromeDriver(chromeOptionsToProxy));
        }

        List<Map<String, Object>> finalProxies = proxies;
        AtomicReference<Proxy> proxyTestado = new AtomicReference<Proxy>();
        proxyTestado.set(null);
        System.out.println("==========================================================ANALISE=================================================================");
        nomesMoedas.forEach(nm -> {
            Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
            System.setProperty("webdriver.chrome.silentOutput", "true");
            ChromeOptions chromeOptions = chromeOptionsSemHeadLess();
            if (usarProxy) {
                while (proxyTestado.get() == null) {
                    ChromeDriver teste = null;
                    try {
                        Random random = new Random();
                        int randomNumber = random.nextInt(finalProxies.size());

                        Proxy proxy = new Proxy();
                        proxy.setHttpProxy(finalProxies.get(randomNumber).get("IP Address").toString() + ":" + finalProxies.get(randomNumber).get("Port").toString());
                        proxy.setSslProxy(finalProxies.get(randomNumber).get("IP Address").toString() + ":" + finalProxies.get(randomNumber).get("Port").toString());
                        ChromeOptions chromeOptionsToTeste = chromeOptionsComHeadLess();
                        chromeOptionsToTeste.setProxy(proxy);
                        teste = new ChromeDriver(chromeOptionsToTeste);
                        teste.get("https://www.google.com");
                        if (teste.getTitle().equals("Google")) {
                            proxyTestado.set(proxy);
                            teste.close();
                            break;
                        } else {
                            teste.close();
                        }
                    } catch (Exception ex) {
                        teste.close();
                    }
                }
                chromeOptions.setProxy(proxyTestado.get());
            }
            WebDriver chromeWebDriver = new ChromeDriver(chromeOptions);
            try {
//                List<String> buttonsToClick = Arrays.asList("//button/span[text()='1 SEM.' or text()='1W']", "//button/span[text()='1 MÊS' or text()='1M']");
                List<String> buttonsToClick = Arrays.asList("//button/span[text()='1 SEM.' or text()='1W']");
                chromeWebDriver.get("https://www.coinbase.com/pt-br/price/" + nm);
                for (String buttonToClick : buttonsToClick) {
                    List<Double> covariancias = new ArrayList<>();
                    List<Double> lagranges = new ArrayList<>();
                    List<Double> valores = new ArrayList<>();
                    List<Double> variacaoVolume = new ArrayList<>();
                    WebDriverWait wait = new WebDriverWait(chromeWebDriver, Duration.ofSeconds(20));
                    for (int i = 0; i < 10; i++) {
                        WebElement elementClicSemana2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(buttonToClick)));
                        if (wait.until(ExpectedConditions.elementToBeClickable(By.xpath(buttonToClick))).getAttribute("style").contains("rgb")) {
                            break;
                        }
                        for(int j = 0; j < 3; j++){
                            try {
                                elementClicSemana2.click();
                                break;
                            } catch (Exception ex){
                                elementClicSemana2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(buttonToClick)));
                                if(j==2){
                                    throw ex;
                                }
                            }
                        }
                    }
                    List<WebElement> elements = null;
                    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div > svg > g > path:nth-child(1)")));
                    List<String> input = Arrays.stream(element.getAttribute("d").split("L")).map(val -> val.replace("M", "")).collect(Collectors.toList());
                    List<Double> finalVariacaoVolume = variacaoVolume;
                    List<Double> finalValores = valores;
                    List<Double> finalValores2 = finalValores;
                    List<Double> finalVariacaoVolume2 = finalVariacaoVolume;
                    input.forEach(val -> {
                        String[] valSpliter = val.split(",");
                        finalValores2.add(Double.valueOf(valSpliter[0]));
                        finalVariacaoVolume2.add(Double.valueOf(valSpliter[1]));
                    });
                    Collections.reverse(variacaoVolume);

                    Double valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum() / valores.size()).doubleValue();
                    Double valorMediaVolume = Double.valueOf(variacaoVolume.stream().mapToDouble(Double::doubleValue).sum() / variacaoVolume.size()).doubleValue();
                    Double varianciaValores = variancia(valores, valorMedia);
                    Double varianciaVolume = variancia(variacaoVolume, valorMediaVolume);
                    Double covariancia = covariancia(valores, variacaoVolume, valorMedia, valorMediaVolume, varianciaValores, varianciaVolume);
                    covariancias.add(covariancia);
                    Double lagrangeValor = predicaoPolinomialLagrange(valores, variacaoVolume);
                    lagranges.add(lagrangeValor);
                    //AJUSTES USANHO APACHE MATH
                    PolynomialFunction polynomialFunction = null;

                    int grauPolinomio = 1;
                    for (int auxGrau = 1; auxGrau < valores.size(); auxGrau++) {
                        polynomialFunction = regressaoPolinomial(valores.stream().mapToDouble(Double::doubleValue).toArray(), variacaoVolume.stream().mapToDouble(Double::doubleValue).toArray(), auxGrau);
                        if (polynomialFunction.toString().contains("NaN")) {
                            grauPolinomio = auxGrau - 1;
                            break;
                        }
                    }
                    polynomialFunction = regressaoPolinomial(valores.stream().mapToDouble(Double::doubleValue).toArray(), variacaoVolume.stream().mapToDouble(Double::doubleValue).toArray(), grauPolinomio);

                    int melhorGrau = grauPolinomio;
                    double menorDiferenca = variacaoVolume.get(variacaoVolume.size() - 1) - polynomialFunction.value(valores.get(valores.size() - 1));
                    if (menorDiferenca < 0) {
                        menorDiferenca = -menorDiferenca;
                    }
                    for (int auxGrau = 1; auxGrau < grauPolinomio; auxGrau++) {
                        PolynomialFunction polynomialFunctionAux = regressaoPolinomial(valores.stream().mapToDouble(Double::doubleValue).toArray(), variacaoVolume.stream().mapToDouble(Double::doubleValue).toArray(), auxGrau);
                        double auxVariacao = 0;
                        for(int i = 0; i < valores.size(); i++){
                            auxVariacao = auxVariacao + variacaoVolume.get(i) - polynomialFunctionAux.value(valores.get(i));
                        }
                        auxVariacao = auxVariacao/valores.size();
                        if (auxVariacao < 0) {
                            auxVariacao = -auxVariacao;
                        }
                        if (menorDiferenca > auxVariacao) {
                            menorDiferenca = auxVariacao;
                            melhorGrau = auxGrau;
                        }
                    }
                    polynomialFunction = regressaoPolinomial(valores.stream().mapToDouble(Double::doubleValue).toArray(), variacaoVolume.stream().mapToDouble(Double::doubleValue).toArray(), melhorGrau);

                    Boolean isAproveitavel = true;
                    Integer isAproveitavelNum = 0;
                    if(filtrarDados) {
                        for (int i = 0; i < valores.size(); i++) {
                            double auxVariacao = variacaoVolume.get(i) - polynomialFunction.value(valores.get(i));
                            if (auxVariacao < 0) {
                                auxVariacao = -auxVariacao;
                            }
                            if (auxVariacao > 50) {
                                isAproveitavel = false;
                                isAproveitavelNum += 1;
                                break;
                            }
                        }
                    }
                    Double derivada = null;
                    Integer aux = null;
                    if (Double.valueOf(valores.size() - isAproveitavelNum)/Double.valueOf(valores.size()) >= 0.99) {
                        derivada = polynomialFunction.derivative().value(valores.get(valores.size() - 1));
                        mapResultado.put(nm, "1S: " + String.format("%.4f", covariancia) + " 1ASL: " + lagrangeValor + " 1SX: " + valores.get(valores.size()-1) + " 1SV: " + polynomialFunction.toString() + " 1SDV: " + derivada);
                        Date dataAtual = new Date();
                        SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String dataHoraFormatada = formatoDataHora.format(dataAtual);

                        mapResultado.put(nm, mapResultado.get(nm) + " | valor: " + chromeWebDriver.findElement(By.xpath("//*[contains(@class,'PriceTickerContainer')]")).getAttribute("data-value") + " | DATA: " + dataHoraFormatada);
                        System.out.println("=> " + nm + "=" + mapResultado.get(nm));
//                        if(derivada < 2 && derivada > 0 && polynomialFunction.polynomialDerivative().derivative().value(valores.get(valores.size() - 1)) > 0) {
//                            for (int i = 1; i < 1000000; i++) {
//                                String valorStr = Double.valueOf(Math.abs(polynomialFunction.derivative().value(valores.get(valores.size() - 1) + i))).toString();
//                                if (Math.abs(derivada) > Math.abs(polynomialFunction.derivative().value(valores.get(valores.size() - 1) + i))) {
////                                if(valorStr.startsWith("0.")){
//                                    aux = i;
//                                    break;
//                                }
//                            }
////                            Double valorizacao = 100 * (polynomialFunction.value((buttonToClick.equals("//button/span[text()='1 SEM.' or text()='1W']")?48:12) * (valores.get(valores.size() - 1) / valores.size()) + valores.get(valores.size() - 1)) - variacaoVolume.get(variacaoVolume.size() - 1)) / variacaoVolume.get(variacaoVolume.size() - 1);
//                            if(aux != null) {
//                                Double valorizacao = 100 * (polynomialFunction.value((aux/valores.get(valores.size() - 1)/valores.size()) * (valores.get(valores.size() - 1) / valores.size()) + valores.get(valores.size() - 1)) - variacaoVolume.get(variacaoVolume.size() - 1)) / variacaoVolume.get(variacaoVolume.size() - 1);
//                                if (buttonToClick.equals("//button/span[text()='1 SEM.' or text()='1W']")) {
//                                    mapResultado.put(nm, "1S: " + String.format("%.4f", covariancia) + " 1ASL: " + lagrangeValor + " 1SV: " + valorizacao + " 1SDV: " + derivada + " 1STV: " + (aux/valores.get(valores.size() - 1)/valores.size()) * 30 + "min VENDER");
//                                } else {
//                                    mapResultado.put(nm, "1M: " + String.format("%.4f", covariancia) + " 1ML: " + lagrangeValor + " 1MV: " + valorizacao + " 1MDV: " + derivada + " 1MTV: " + (aux/valores.get(valores.size() - 1)/valores.size()) * 30 + "min VENDER");
//                                }
//
//                                Date dataAtual = new Date();
//                                SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                                String dataHoraFormatada = formatoDataHora.format(dataAtual);
//
//                                mapResultado.put(nm, mapResultado.get(nm) + " | valor: " + chromeWebDriver.findElement(By.xpath("//*[@id=\"PriceSection\"]/div[1]/div[1]/div[1]/div[1]/div[2]/div/span")).getText() + " | DATA: " + dataHoraFormatada);
//                                System.out.println("=> " + nm + "=" + mapResultado.get(nm));
//                                mapResultado.remove(nm);
//                            }
//                        }
//                        else if(derivada > -2 && derivada < 0){
//                            for (int i = 1; i < 1000000; i++) {
//                                String valorStr = Double.valueOf(Math.abs(polynomialFunction.derivative().value(valores.get(valores.size() - 1) + i))).toString();
////                                if (Math.abs(derivada) > Math.abs(polynomialFunction.derivative().value(valores.get(valores.size() - 1) + i))) {
//                                if(valorStr.contains("E")) {
//                                    aux = i;
//                                    break;
//                                }
//                            }
////                            Double valorizacao = 100 * (polynomialFunction.value((buttonToClick.equals("//button/span[text()='1 SEM.' or text()='1W']")?48:12) * (valores.get(valores.size() - 1) / valores.size()) + valores.get(valores.size() - 1)) - variacaoVolume.get(variacaoVolume.size() - 1)) / variacaoVolume.get(variacaoVolume.size() - 1);
//                            if(aux != null) {
//                                Double valorizacao = 100 * (polynomialFunction.value((aux/valores.get(valores.size() - 1)/valores.size()) * (valores.get(valores.size() - 1) / valores.size()) + valores.get(valores.size() - 1)) - variacaoVolume.get(variacaoVolume.size() - 1)) / variacaoVolume.get(variacaoVolume.size() - 1);
//                                if (buttonToClick.equals("//button/span[text()='1 SEM.' or text()='1W']")) {
//                                    mapResultado.put(nm, "1S: " + String.format("%.4f", covariancia) + " 1ASL: " + lagrangeValor + " 1SV: " + valorizacao + " 1SDV: " + derivada + " 1STV: " + (aux/valores.get(valores.size() - 1)/valores.size()) * 30 + "min COMPRAR");
//                                } else {
//                                    mapResultado.put(nm, "1M: " + String.format("%.4f", covariancia) + " 1ML: " + lagrangeValor + " 1MV: " + valorizacao + " 1MDV: " + derivada + " 1MTV: " + (aux/valores.get(valores.size() - 1)/valores.size()) * 30 + "min COMPRAR");
//                                }
//
//                                Date dataAtual = new Date();
//                                SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                                String dataHoraFormatada = formatoDataHora.format(dataAtual);
//
//                                mapResultado.put(nm, mapResultado.get(nm) + " | valor: " + chromeWebDriver.findElement(By.xpath("//*[@id=\"PriceSection\"]/div[1]/div[1]/div[1]/div[1]/div[2]/div/span")).getText() + " | DATA: " + dataHoraFormatada);
//                                System.out.println("=> " + nm + "=" + mapResultado.get(nm));
//                                mapResultado.remove(nm);
//                            }
//                        }

                    }


                }
                chromeWebDriver.close();
            } catch (Exception e) {
                e.printStackTrace();
                proxyTestado.set(null);
                chromeWebDriver.close();
            }
        });

    }

    public String moda(List<Double> valores) {
        String retorno = "Moda: ";
        AtomicReference<Integer> quantidade = new AtomicReference<>(0);
        AtomicReference<Double> moda = new AtomicReference<>(0.0d);
        valores.stream().forEach(x -> {
            Integer quantidadeValor = (valores.stream().filter(y -> y.equals(x)).collect(Collectors.toList())).size();
            quantidade.set(quantidade.get() > quantidadeValor ? quantidade.get() : quantidadeValor);
            moda.set(quantidade.get() > quantidadeValor ? moda.get() : x);
        });
        retorno += moda.get().toString() + "(" + String.format("%.2f", (Double.valueOf(quantidade.get()).doubleValue() / Double.valueOf(valores.size()).doubleValue()) * 100) + "%)";
        return retorno;
    }

    public Double modaValue(List<Double> valores) {
        AtomicReference<Integer> quantidade = new AtomicReference<>(0);
        AtomicReference<Double> moda = new AtomicReference<>(0.0d);
        valores.stream().forEach(x -> {
            Integer quantidadeValor = (valores.stream().filter(y -> y.equals(x)).collect(Collectors.toList())).size();
            quantidade.set(quantidade.get() > quantidadeValor ? quantidade.get() : quantidadeValor);
            moda.set(quantidade.get() > quantidadeValor ? moda.get() : x);
        });
        return moda.get();
    }

    public Double variancia(List<Double> valores, Double media) {
        Double numerador = 0d;
        for (int i = 0; i < valores.size(); i++) {
            numerador = numerador + Math.pow(valores.get(i) - media, 2);
        }
        return Math.sqrt(numerador / (valores.size() - 1));
    }

    public Double covariancia(List<Double> valores, List<Double> volume, Double mediaValores, Double mediaVolume, Double varianciaValores, Double varianciaVolume) {
        Double numerador = 0d;
        for (int i = 0; i < valores.size(); i++) {
            numerador = numerador + (valores.get(i) - mediaValores) * (volume.get(i) - mediaVolume);
        }
        Double denominador = Double.valueOf(valores.size() - 1);

        return (numerador / denominador) / (varianciaValores * varianciaVolume);
    }

    public Double isNormal(List<Double> valores, Double variancia, Double media) {
        List<Double> valoresDentroDaNormal = valores.stream().filter(x -> x > media - variancia && x < media + variancia).collect((Collectors.toList()));
        return (Double.valueOf(valoresDentroDaNormal.size()).doubleValue() / Double.valueOf(valores.size()).doubleValue()) * 100;
    }


    public String headShoulderPattern(List<Double> valores) {
        int i = valores.size() - 1;
        int aux = 1;
        while (i - 5 * aux > 0) {
            if ((valores.get(i) > valores.get(i - 1 * aux)) && (valores.get(i - 1 * aux) < valores.get(i - 2 * aux)) && (valores.get(i - 2 * aux) > valores.get(i - 3 * aux)) && (valores.get(i - 3 * aux) < valores.get(i - 4 * aux)) && (valores.get(i - 4 * aux) > valores.get(i - 5 * aux)) &&
                    (new BigDecimal(valores.get(i - 2 * aux) - valores.get(i - 4 * aux)).setScale(1, RoundingMode.HALF_EVEN)).doubleValue() == 0.0) {
                return "HS - BAIXA(" + aux + ")";
            }
            aux++;
        }

        return "HS - NÃO ENCONTRADO";
    }

    public String headShoulderReversePattern(List<Double> valores) {
        int i = valores.size() - 1;
        int aux = 1;
        while (i - 5 * aux > 0) {
            if ((valores.get(i) < valores.get(i - 1 * aux)) && (valores.get(i - 1 * aux) > valores.get(i - 2 * aux)) && (valores.get(i - 2 * aux) < valores.get(i - 3 * aux)) && (valores.get(i - 3 * aux) > valores.get(i - 4 * aux)) && (valores.get(i - 4 * aux) < valores.get(i - 5 * aux)) &&
                    (new BigDecimal(valores.get(i - 2 * aux) - valores.get(i - 4 * aux)).setScale(1, RoundingMode.HALF_EVEN)).doubleValue() == 0.0) {
                return "HSR - ALTA(" + aux + ")";
            }
            aux++;
        }

        return "HSR - NÃO ENCONTRADO";
    }

    public String cunhaDeBaixaPattern(List<Double> valores) {
        int i = valores.size() - 1;
        int aux = 1;
        while (i - 9 * aux > 0) {
            if ((valores.get(i) > valores.get(i - 1 * aux)) && (valores.get(i - 1 * aux) < valores.get(i - 2 * aux)) && (valores.get(i - 2 * aux) > valores.get(i - 3 * aux)) && (valores.get(i - 3 * aux) < valores.get(i - 4 * aux)) && (valores.get(i - 4 * aux) > valores.get(i - 5 * aux)) &&
                    (valores.get(i - 5 * aux) < valores.get(i - 6 * aux)) && (valores.get(i - 6 * aux) > valores.get(i - 7 * aux)) && (valores.get(i - 7 * aux) < valores.get(i - 8 * aux))
                    && ((valores.get(i) < valores.get(i - 2 * aux)) && (valores.get(i - 2 * aux) < valores.get(i - 4 * aux)) && (valores.get(i - 4 * aux) < valores.get(i - 6 * aux)) && (valores.get(i - 6 * aux) < valores.get(i - 8 * aux)))) {
                return "CUNHA DE BAIXA - ALTA(" + aux + ")";
            }
            aux++;
        }

        return "CUNHA DE BAIXA - NÃO ENCONTRADO";
    }

    public String trinaguloDeReversaoPattern(List<Double> valores) {
        int i = valores.size() - 1;
        int aux = 1;
        while (i - 9 * aux > 0) {
            if ((valores.get(i) < valores.get(i - 1 * aux)) && (valores.get(i - 1 * aux) > valores.get(i - 2 * aux)) && (valores.get(i - 2 * aux) < valores.get(i - 3 * aux)) && (valores.get(i - 3 * aux) > valores.get(i - 4 * aux)) && (valores.get(i - 4 * aux) < valores.get(i - 5 * aux)) &&
                    (valores.get(i - 5 * aux) > valores.get(i - 6 * aux)) && (valores.get(i - 6 * aux) < valores.get(i - 7 * aux)) && (valores.get(i - 7 * aux) > valores.get(i - 8 * aux))
                    && ((valores.get(i) > valores.get(i - 2 * aux)) && (valores.get(i - 2 * aux) > valores.get(i - 4 * aux)) && (valores.get(i - 4 * aux) > valores.get(i - 6 * aux)) && (valores.get(i - 6 * aux) > valores.get(i - 8 * aux)))) {
                return "TRI. REV. - BAIXA(" + aux + ")";
            }
            aux++;
        }

        return "TRI. REV. - NÃO ENCONTRADO";
    }

    public String cestoBasePattern(List<Double> valores, List<Double> data) {
        int i = valores.size() - 2;
        Double valorAtual = valores.get(i);
        Integer indexOcorrenciaAnterior = null;
        int quantidadeElementos = 1;
        for (int j = i - 1; j > -1; j--) {
            quantidadeElementos++;
            if (valores.get(j).doubleValue() > valorAtual.doubleValue()) {
                indexOcorrenciaAnterior = j;
                break;
            }
        }
        if (quantidadeElementos != 1 && indexOcorrenciaAnterior != null) {
            int aux = 1;
            while (aux < quantidadeElementos - 1) {
                if (quantidadeElementos % aux == 0) {
                    int quantidadeSubida = 0;
                    int quantidadeDescida = 0;
                    Double minValue = 0d;
                    for (int j = indexOcorrenciaAnterior; j < valores.size() - 1; j += aux) {
                        if ((valores.get(j).doubleValue() < minValue.doubleValue()) || minValue.doubleValue() == 0d) {
                            minValue = valores.get(j);
                        }
                        if (j + aux < i &&
                                valores.get(j).doubleValue() < valores.get(j + aux).doubleValue() &&
                                (valores.get(j).doubleValue() <= valores.get(indexOcorrenciaAnterior).doubleValue() || valores.get(j).doubleValue() <= valorAtual.doubleValue()) &&
                                (valores.get(j + aux).doubleValue() <= valores.get(indexOcorrenciaAnterior).doubleValue() || valores.get(j + aux).doubleValue() <= valorAtual.doubleValue())) {
                            quantidadeSubida++;
                        } else if (j + aux < i &&
                                valores.get(j).doubleValue() > valores.get(j + aux).doubleValue() &&
                                (valores.get(j).doubleValue() <= valores.get(indexOcorrenciaAnterior).doubleValue() || valores.get(j).doubleValue() <= valorAtual.doubleValue()) &&
                                (valores.get(j + aux).doubleValue() <= valores.get(indexOcorrenciaAnterior).doubleValue() || valores.get(j + aux).doubleValue() <= valorAtual.doubleValue())) {
                            quantidadeDescida++;
                        }
                    }
                    if (quantidadeSubida > 0 && quantidadeDescida > 0 && minValue.doubleValue() < valorAtual.doubleValue()) {
                        return "CESTO BASE - ALTA(" + data.get(indexOcorrenciaAnterior) + "," + aux + "," + data.get(i + 1) + ")";
                    }
                }
                aux++;
            }
        }

        return "CESTO BASE - NÃO ENCONTRADO";
    }

    public String predicaoPolinomial(List<Double> variacaoValores, List<Double> variacaoVolume) {
        int quantZeros = variacaoVolume.stream().filter(x -> x.equals(0)).collect(Collectors.toList()).size();
        quantZeros = (variacaoVolume.get(variacaoVolume.size() - 1)).equals(0) ? (quantZeros - 1) : quantZeros;
        List<Double> zerosFunc = new ArrayList<>();
        if (quantZeros > 0) {
            for (int i = 0; i < variacaoVolume.size(); i++) {
                if (variacaoVolume.get(i).equals(0)) {
                    zerosFunc.add(variacaoValores.get(i));
                }
            }
            Double valorAtual = Double.valueOf(1.0);
            Double valorImediatamenteAnterior = Double.valueOf(1.0);
            for (int i = 0; i < zerosFunc.size(); i++) {
                valorAtual = valorAtual * (variacaoValores.get(variacaoValores.size() - 1) - zerosFunc.get(i));
                valorImediatamenteAnterior = valorImediatamenteAnterior * ((variacaoValores.get(variacaoValores.size() - 1) - 0.000000000000001) - zerosFunc.get(i));
            }
            return "PREDICAO POLINOMIAL - (" + 0.000000000000001 / (valorAtual.doubleValue() - valorImediatamenteAnterior.doubleValue()) + ") (" + String.valueOf(quantZeros) + ")";
        } else {
            return "PREDICAO POLINOMIAL - NÃO ENCONTRADO (" + String.valueOf(quantZeros) + ")";
        }
    }

    public Double predicaoPolinomialLagrange(List<Double> variacaoValores, List<Double> variacaoVolume) {
        Double valorAtual = Double.valueOf(0.0);
        Double valorImediatamenteAnterior = Double.valueOf(0.0);
        for (int i = 0; i < variacaoVolume.size(); i++) {
            Double incrementoAtual = lagrange(variacaoValores.get(variacaoValores.size() - 1), variacaoValores, i) * variacaoVolume.get(i);
            valorAtual += Double.isNaN(incrementoAtual) ? Double.valueOf(0.0) : incrementoAtual;
            Double incrementoAnterior = lagrange(variacaoValores.get(variacaoValores.size() - 1) - 0.000000000001, variacaoValores, i) * variacaoVolume.get(i);
            valorImediatamenteAnterior += Double.isNaN(incrementoAnterior) ? Double.valueOf(0.0) : incrementoAnterior;
        }
        /*Double valorAtualToMax = valorAtual;
        Double valorMax = Double.valueOf(0.0);
        Double valorVariacaoReferencia = variacaoValores.get(variacaoValores.size()-1);
        boolean escape = true;
        if(0.000000000000001/(valorAtual.doubleValue() - valorImediatamenteAnterior.doubleValue()) > 0){
            while(escape){
                variacaoValores.add(valorVariacaoReferencia);
                variacaoVolume.add(Integer.valueOf((int) Math.round(valorAtualToMax.doubleValue())));
                valorMax = valorAtualToMax;
                for(int i = 0; i < variacaoVolume.size(); i++){
                    Double incremento = lagrange(valorVariacaoReferencia, variacaoValores, i)*variacaoVolume.get(i);
                    valorAtualToMax += Double.isNaN(incremento) ? Double.valueOf(0.0) : incremento;
                }
                if(valorAtualToMax < valorMax){
                    escape = false;
                }
                variacaoValores = variacaoValores.subList(0, variacaoValores.size()-1);
                variacaoVolume = variacaoVolume.subList(0, variacaoVolume.size()-1);
                valorVariacaoReferencia += 0.001;
            }
        }*/

        return 0.000000000001 / (valorAtual.doubleValue() - valorImediatamenteAnterior.doubleValue());
    }

    private Double lagrange(Double valorXAtual, List<Double> variacaoValores, int index) {
        Double retorno = Double.valueOf(1.0);
        for (int i = 0; i < variacaoValores.size(); i++) {
            if (i != index) {
                if (variacaoValores.get(index) - variacaoValores.get(i) != 0) {
                    retorno = retorno * ((valorXAtual - variacaoValores.get(i)) / (variacaoValores.get(index) - variacaoValores.get(i)));
                }
            }
        }
        return retorno;
    }

    public Double predicaoPolinomialNewton(List<Double> variacaoValores, List<Double> variacaoVolume) {
        Double valorAtual = Double.valueOf(variacaoVolume.get(0));
        Double valorImediatamenteAnterior = Double.valueOf(variacaoVolume.get(0));
        for (int i = 0; i < variacaoVolume.size(); i++) {
            Double incrementoAtual = newton(variacaoValores, variacaoVolume, i) * multiplicador(variacaoValores, variacaoValores.get(variacaoValores.size() - 1), i);
            valorAtual += Double.isNaN(incrementoAtual) ? Double.valueOf(0.0) : incrementoAtual;
            Double incrementoAnterior = newton(variacaoValores, variacaoVolume, i) * multiplicador(variacaoValores, variacaoValores.get(variacaoValores.size() - 1) - 0.0000000000001, i);
            valorImediatamenteAnterior += Double.isNaN(incrementoAnterior) ? Double.valueOf(0.0) : incrementoAnterior;
        }
        /*Double valorAtualToMax = valorAtual;
        Double valorMax = Double.valueOf(0.0);
        Double valorVariacaoReferencia = variacaoValores.get(variacaoValores.size()-1);
        boolean escape = true;
        if(0.000000000000001/(valorAtual.doubleValue() - valorImediatamenteAnterior.doubleValue()) > 0){
            while(escape){
                variacaoValores.add(valorVariacaoReferencia);
                variacaoVolume.add(Integer.valueOf((int) Math.round(valorAtualToMax.doubleValue())));
                valorMax = valorAtualToMax;
                for(int i = 0; i < variacaoVolume.size(); i++){
                    Double incremento = newton(variacaoValores, variacaoVolume, i)*multiplicador(variacaoValores, valorVariacaoReferencia, i);
                    valorAtualToMax += Double.isNaN(incremento) ? Double.valueOf(0.0):incremento;
                }
                if(valorAtualToMax < valorMax){
                    escape = false;
                }
                variacaoValores = variacaoValores.subList(0, variacaoValores.size()-1);
                variacaoVolume = variacaoVolume.subList(0, variacaoVolume.size()-1);
                valorVariacaoReferencia += 0.001;
            }
        }*/
        return 0.0000000000001 / (valorAtual.doubleValue() - valorImediatamenteAnterior.doubleValue());
    }

    public Double multiplicador(List<Double> variacaoValores, Double valorXAtual, int index) {
        Double retorno = Double.valueOf(1);
        if (index == 0) {
            return retorno;
        }
        for (int i = 0; i < index; i++) {
            retorno = retorno * (valorXAtual - variacaoValores.get(i));
        }
        return retorno;
    }

    public Double newton(List<Double> variacaoValores, List<Double> variacaoVolumes, int index) {
        if (index == 0) {
            /*contadorRecursao++;
        	System.out.println(contadorRecursao);*/
            return Double.valueOf(variacaoVolumes.get(0));
        } else {
            Double valorFinal = variacaoValores.get(variacaoValores.size() - 1);
            Double valorInicial = variacaoValores.get(0);
            List<Double> novaVariacaoValoresDireita = variacaoValores.subList(0, variacaoValores.size() - 1);
            List<Double> novaVariacaoVolumesDireita = variacaoVolumes.subList(0, variacaoVolumes.size() - 1);
            List<Double> novaVariacaoValoresEsquerda = variacaoValores.subList(1, variacaoValores.size());
            List<Double> novaVariacaoVolumesEsquerda = variacaoVolumes.subList(1, variacaoVolumes.size());
            if ((valorFinal.doubleValue() - valorInicial.doubleValue()) != 0) {
                return ((newton(novaVariacaoValoresEsquerda, novaVariacaoVolumesEsquerda, index - 1).doubleValue() - newton(novaVariacaoValoresDireita, novaVariacaoVolumesDireita, index - 1).doubleValue()) /
                        (valorFinal.doubleValue() - valorInicial.doubleValue()));
            } else {
                return 1.0;
            }

        }
    }


    public List<String> comparar(List<String> maisAtivos, List<String> maisGanhos) {
        List<String> retorno = new ArrayList<>();
        maisAtivos.stream().forEach(x -> {
            if (maisGanhos.contains(x)) {
                retorno.add(x);
            }
        });
        return retorno;
    }

    public void processaOdds(String html) {
        List<String> casasConfiaveis = Arrays.stream(new String[]{"Bet365", "Betnacional", "Bodog", "SportingBet", "PixBet", "Esportedasorte", "Betano", "1xBet", "Betfair", "PingolBet"}).collect(Collectors.toList());
        Document document = Jsoup.parse(html);
        Elements linksGoogle = document.select("div[class*=cursor-pointer border rounded-md mb-4 px-1 py-2 flex flex-col lg:flex-row relative]");
        /*((TextNode) element.childNode(1).childNode(1).childNode(0)).text().contains("31 mar") &&*/
        List<Element> filtrado = linksGoogle.stream().filter(element ->
                Double.valueOf(((TextNode) element.childNode(3).childNode(1).childNode(5).childNode(0)).text()) > 2 &&
                        Double.valueOf(((TextNode) element.childNode(3).childNode(3).childNode(5).childNode(0)).text()) > 2 &&
                        casasConfiaveis.contains(element.childNode(3).childNode(1).childNode(3).attr("alt")) &&
                        casasConfiaveis.contains(element.childNode(3).childNode(3).childNode(3).attr("alt"))).collect(Collectors.toList());

        filtrado.forEach(element -> {
            System.out.println(((TextNode) element.childNode(1).childNode(3).childNode(1).childNode(0)).text()
                    + " / " + ((TextNode) element.childNode(3).childNode(1).childNode(5).childNode(0)).text() + " - " + ((TextNode) element.childNode(3).childNode(3).childNode(5).childNode(0)).text()
                    + " / " + element.childNode(3).childNode(1).childNode(3).attr("alt") + " - " + element.childNode(3).childNode(3).childNode(3).attr("alt"));
        });

    }

    private List<Map<String, Object>> getFreeProxies(WebDriver driver) {
        driver.get("https://sslproxies.org");

        WebElement table = driver.findElement(By.tagName("table"));
        List<WebElement> thead = table.findElement(By.tagName("thead")).findElements(By.tagName("th"));
        List<WebElement> tbody = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

        List<String> headers = new ArrayList<>();
        for (WebElement th : thead) {
            headers.add(th.getText().strip());
        }

        List<Map<String, Object>> proxies = new ArrayList<>();
        for (WebElement tr : tbody) {
            Map<String, Object> proxyData = new HashMap<>();
            List<WebElement> tds = tr.findElements(By.tagName("td"));
            for (int i = 0; i < headers.size(); i++) {
                proxyData.put(headers.get(i), tds.get(i).getText().strip());
                proxies.add(proxyData);
            }
        }
        driver.close();
        return proxies;
//        return proxies.stream().filter(val -> Arrays.asList("US", "Singapura", "India").contains(val.get("Country").toString().equals("US")) && !val.get("Last Checked").toString().contains("hours")).collect(Collectors.toList());
//        return proxies.stream().filter(val -> !val.get("Last Checked").toString().contains("hours")).collect(Collectors.toList());
    }

    public ChromeOptions chromeOptionsSemHeadLess() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(new String[]{"--remote-allow-origins=*"});
        chromeOptions.addArguments(new String[]{"test-type"});
        chromeOptions.addArguments(new String[]{"start-maximized"});
        chromeOptions.addArguments(new String[]{"disable-web-security"});
        chromeOptions.addArguments(new String[]{"allow-running-insecure-content"});
        chromeOptions.addArguments(new String[]{"--log-level=3"});
        chromeOptions.addArguments(new String[]{"--ignore-ssl-errors=yes"});
        chromeOptions.addArguments(new String[]{"--ignore-certificate-errors"});
        chromeOptions.addArguments(new String[]{"--kiosk-printing"});
        chromeOptions.addArguments(new String[]{"--lang=pt-BR"});
        chromeOptions.addArguments(new String[]{"--disable-gpu"});
        chromeOptions.addArguments(new String[]{"--disable-dev-shm-usage"});
        chromeOptions.addArguments(new String[]{"--no-sandbox"});
        chromeOptions.addArguments(new String[]{"--disable-session-crashed-bubble"});
        chromeOptions.addArguments(new String[]{"--no-sandbox"});
        chromeOptions.addArguments(new String[]{"--disable-dev-shm-usage"});
        chromeOptions.addArguments(new String[]{"--disable-blink-features=AutomationControlled"});
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptions.setExperimentalOption("useAutomationExtension", (Object) null);
        chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
        chromeOptions.setAcceptInsecureCerts(true);

        return chromeOptions;
    }

    public ChromeOptions chromeOptionsComHeadLess() {
        ChromeOptions chromeOptionsToProxy = new ChromeOptions();
        chromeOptionsToProxy.addArguments(new String[]{"--remote-allow-origins=*"});
        chromeOptionsToProxy.addArguments(new String[]{"test-type"});
        chromeOptionsToProxy.addArguments(new String[]{"start-maximized"});
        chromeOptionsToProxy.addArguments(new String[]{"disable-web-security"});
        chromeOptionsToProxy.addArguments(new String[]{"allow-running-insecure-content"});
        chromeOptionsToProxy.addArguments(new String[]{"--log-level=3"});
        chromeOptionsToProxy.addArguments(new String[]{"--ignore-ssl-errors=yes"});
        chromeOptionsToProxy.addArguments(new String[]{"--ignore-certificate-errors"});
        chromeOptionsToProxy.addArguments(new String[]{"--kiosk-printing"});
        chromeOptionsToProxy.addArguments(new String[]{"--lang=pt-BR"});
        chromeOptionsToProxy.addArguments(new String[]{"--disable-gpu"});
        chromeOptionsToProxy.addArguments(new String[]{"--disable-dev-shm-usage"});
        chromeOptionsToProxy.addArguments(new String[]{"--no-sandbox"});
        chromeOptionsToProxy.addArguments(new String[]{"--disable-session-crashed-bubble"});
        chromeOptionsToProxy.addArguments(new String[]{"--no-sandbox"});
        chromeOptionsToProxy.addArguments(new String[]{"--disable-dev-shm-usage"});
        chromeOptionsToProxy.addArguments(new String[]{"--disable-blink-features=AutomationControlled"});
        chromeOptionsToProxy.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptionsToProxy.setExperimentalOption("useAutomationExtension", (Object) null);
        chromeOptionsToProxy.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
        chromeOptionsToProxy.setAcceptInsecureCerts(true);
        chromeOptionsToProxy.addArguments("--headless");

        return chromeOptionsToProxy;
    }

    public static void sendMessageOauth(Message email, String userId) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, GmailAuthService.getCredentials())
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
            com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
            message.setRaw(encodedEmail);
            message = service.users().messages().send(userId, message).execute();

            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);


        return email;
    }


    public static void main(String[] args) {
        double value = 11.18;
        double variacaoEstimada = -0.0020639 * Math.pow(value, 2.0) + 0.1023252 * value + 0.1423548;
        System.out.println(variacaoEstimada);
        System.out.println(value * (1 + variacaoEstimada / 100));
    }

    // Método para calcular a regressão polinomial
    public static PolynomialFunction regressaoPolinomial(double[] x, double[] y, int grau) {
        int n = x.length;

        // Criação da matriz X para ajuste polinomial
        double[][] matrizX = new double[n][grau + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= grau; j++) {
                matrizX[i][j] = Math.pow(x[i], j);
            }
        }

        // Executa a regressão linear usando OLS (mínimos quadrados ordinários)
        OLSMultipleLinearRegression regressao = new OLSMultipleLinearRegression();
        regressao.setNoIntercept(true);  // Já incluímos o intercepto na matriz X
        regressao.newSampleData(y, matrizX);

        // Coeficientes do polinômio ajustado
        double[] coeficientes = regressao.estimateRegressionParameters();

        // Retorna o polinômio ajustado como uma função
        return new PolynomialFunction(coeficientes);
    }

    // Função para calcular o polinômio de Lagrange
    public static PolynomialFunction polinomioLagrange(double[] x, double[] y) {
        int n = x.length;

        // Polinômio resultante (inicialmente zero)
        double[] result = new double[0]; // Usaremos um polinômio vazio inicialmente

        for (int i = 0; i < n; i++) {
            // Inicia o termo de Lagrange L_i(x) como [1]
            double[] term = new double[]{1.0};
            double denominador = 1.0;

            // Montar L_i(x)
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    // Multiplicar o termo por (x - x[j])
                    term = multiplyPolynomials(term, new double[]{-x[j], 1.0});
                    denominador *= (x[i] - x[j]);
                }
            }

            // Multiplicar L_i(x) por y[i] / denominador
            double coeficiente = y[i] / denominador;

            // Multiplicar cada coeficiente do termo pelo coeficiente calculado
            for (int k = 0; k < term.length; k++) {
                term[k] *= coeficiente;
            }

            // Adicionar o termo de Lagrange ao polinômio resultante
            result = addPolynomials(result, term);
        }

        return new PolynomialFunction(result);
    }

    private static double[] addPolynomials(double[] poly1, double[] poly2) {
        int maxLength = Math.max(poly1.length, poly2.length);
        double[] result = new double[maxLength];

        for (int i = 0; i < maxLength; i++) {
            double coef1 = (i < poly1.length) ? poly1[i] : 0;
            double coef2 = (i < poly2.length) ? poly2[i] : 0;
            result[i] = coef1 + coef2;
        }

        return result;
    }

    private static double[] multiplyPolynomials(double[] poly1, double[] poly2) {
        double[] result = new double[poly1.length + poly2.length - 1];

        for (int i = 0; i < poly1.length; i++) {
            for (int j = 0; j < poly2.length; j++) {
                result[i + j] += poly1[i] * poly2[j];
            }
        }

        return result;
    }

    public static PolynomialFunction criarPolinomio(int grau, double[] y) {
        // Verifica se o número de valores y é suficiente para o grau desejado
        if (y.length < grau + 1) {
            throw new IllegalArgumentException("Número de valores de y deve ser pelo menos " + (grau + 1));
        }

        // Matriz de design para os valores x
        double[][] x = new double[y.length][grau + 1];

        // Preenche a matriz de design com potências de x
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j <= grau; j++) {
                x[i][j] = Math.pow(i, j); // x[i] é apenas o índice, você pode modificar conforme necessário
            }
        }

        // Realiza a regressão polinomial
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, x);

        // Obtém os coeficientes do modelo
        double[] coeficientes = regression.estimateRegressionParameters();

        // Cria o PolynomialFunction com os coeficientes
        return new PolynomialFunction(coeficientes);
    }
}
