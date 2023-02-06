package io.reflectoring.financastestefinal.testes.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class TesteService {
    public List<String> PegarConteudoPelaClasse(String html, String nomeClasse){
        List<String> retorno = new ArrayList<>();

        String[] primeiraParte = html.split(nomeClasse);
        List<String> primeiraParteList = Arrays.stream(primeiraParte).toList();
        List<String> primeiraParteListFormat = new ArrayList<>();

        for(int i = 0; i < primeiraParteList.size(); i++){
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
        });
        return retorno;
    }
    public List<String> PegarPercentual(String html, String nomeClasse){
        List<String> retorno = new ArrayList<>();

        String[] primeiraParte = html.split("Diminuiu");
        List<String> primeiraParteList = Arrays.stream(primeiraParte).toList();
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
    public LinkedHashMap<String, String> analiseTemporal(List<String> nomesAcoes, boolean analiseInicial){
        LinkedHashMap<String, String> variacaoMedia = new LinkedHashMap<>();
        nomesAcoes.forEach(nm -> {
            String nomeAcao = "";
            if(!nm.contains(">")){
                nomeAcao = nm;
            }else{
                nomeAcao = nm.split(">")[4];
            }
            var client = HttpClient.newHttpClient();
            var requestMaisAtivos = HttpRequest.newBuilder(
                            URI.create("https://www.google.com/finance/quote/"+nomeAcao+":BVMF?window=5D"))
                    .header("accept", "application/json")
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(requestMaisAtivos, HttpResponse.BodyHandlers.ofString());
                String resposta = response.body();
                String[] primeiraParte = resposta.split("key: 'ds:10'");
                String[] segundaParte = primeiraParte[1].split("sideChannel");
                String terceiraParte = segundaParte[0].substring(60, segundaParte[0].length()-2);
                String[] quartaParte = terceiraParte.split(",");

                String[] primeiraParteMes = resposta.split("key: 'ds:11'");
                String[] segundaParteMes = primeiraParte[1].split("sideChannel");
                String terceiraParteMes = segundaParte[0].substring(60, segundaParte[0].length()-2);
                String[] quartaParteMes = terceiraParte.split(",");

                List<Double> valores = new ArrayList<>();
                List<Integer> valoresVolume = new ArrayList<>();
                for(int i =  quartaParte[25].substring(1).equals("-10800]]") ? 26:25; i < quartaParte.length; i += 15){
                    valores.add(Double.valueOf(quartaParte[i].substring(1)));
                    if(quartaParte[i+6].contains("]]")){
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-2)));
                    }else{
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-1)));
                    }
                }

                List<Double> variacao = new ArrayList<>();
                AtomicReference<Double> aux = new AtomicReference<>(null);
                valores.stream().forEach(x -> {
                    if(aux.get() != null){
                        variacao.add(((x/ aux.get())-1)*100);
                    }
                    aux.set(x);
                });
                Double valorModa = modaValue(valores).doubleValue();
                Double valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();

                if(!analiseInicial) {
                    if((valorModa < valores.get(valores.size() - 1) && valores.get(valores.size() - 1) < valorMedia) ||
                        (valorModa > valores.get(valores.size() - 1) && valores.get(valores.size() - 1) > valorMedia)){
                        System.out.println(nomeAcao+" - possibilidade de venda");
                    }
                variacaoMedia.put(nomeAcao, String.valueOf((variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " , " +
                        String.valueOf(valoresVolume.get(valoresVolume.size() - 1) - valoresVolume.get(valoresVolume.size() - 2)) + " , " +
                        String.valueOf(valoresVolume.get(0) - valoresVolume.get(1)) + ", " +
                        moda(valores) + ", " +
                        "Media - " + (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size() + ", " +
                        "Atual - " + valores.get(valores.size() - 1).toString());
            }

            Double menorValorMediaModa = modaValue(valores).doubleValue() <= Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue() ?
                                            modaValue(valores).doubleValue() : Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();

            if( valores.get(valores.size()-1).doubleValue() < menorValorMediaModa){
                if(analiseInicial){
                    variacaoMedia.put(nomeAcao, String.valueOf((variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " , " +
                            String.valueOf(valoresVolume.get(valoresVolume.size() - 1) - valoresVolume.get(valoresVolume.size() - 2)) + " , " +
                            String.valueOf(valoresVolume.get(0) - valoresVolume.get(1)) + ", " +
                            moda(valores) + ", " +
                            "Media - " + (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size() + ", " +
                            "Atual - " + valores.get(valores.size() - 1).toString());
                }
            }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return variacaoMedia;
    }

    public String moda(List<Double> valores){
        String retorno = "Moda - ";
        AtomicReference<Integer> quantidade = new AtomicReference<>(0);
        AtomicReference<Double> moda = new AtomicReference<>(0.0d);
        valores.stream().forEach(x -> {
            Integer quantidadeValor = (valores.stream().filter(y -> y.equals(x)).collect(Collectors.toList())).size();
            quantidade.set(quantidade.get() > quantidadeValor ? quantidade.get() : quantidadeValor);
            moda.set(quantidade.get() > quantidadeValor ? moda.get() : x);
        });
        retorno += moda.get().toString() + "("+(quantidade.get()/valores.size())*100+"%)";
        return retorno;
    }public Double modaValue(List<Double> valores){
        AtomicReference<Integer> quantidade = new AtomicReference<>(0);
        AtomicReference<Double> moda = new AtomicReference<>(0.0d);
        valores.stream().forEach(x -> {
            Integer quantidadeValor = (valores.stream().filter(y -> y.equals(x)).collect(Collectors.toList())).size();
            quantidade.set(quantidade.get() > quantidadeValor ? quantidade.get() : quantidadeValor);
            moda.set(quantidade.get() > quantidadeValor ? moda.get() : x);
        });
        return moda.get();
    }

    /*public String headShoulderReversePattern(List<Double> valores){
        String retorno = "HSR - FALSE";
        int i = valores.size() -1;
        if((valores.get(i) > valores.get(i-1)) && (valores.get(i-1) < valores.get(i-2)) && (valores.get(i-2) > valores.get(i-3)) && (valores.get(i-3) < valores.get(i-4)) && (valores.get(i-4) > valores.get(i-5)) &&
                (new BigDecimal(valores.get(i-2) - valores.get(i-4)).setScale(1, RoundingMode.HALF_EVEN)).equals(0.0)){
            return "HSR - TRUE";
        }else if((valores.get(i) > valores.get(i-1)) && (valores.get(i) > valores.get(i-2)) && (valores.get(i-2) < valores.get(i-3)) && (valores.get(i-4) > valores.get(i-5)) && (valores.get(i-4) < valores.get(i-5)) && (valores.get(i-5) > valores.get(i-6)) &&
                (new BigDecimal(valores.get(i-3) - valores.get(i-5)).setScale(1, RoundingMode.HALF_EVEN)).equals(0.0)){
            return "HSR - TRUE";
        }
        return retorno;
    }*/


    public List<String> comparar(List<String> maisAtivos, List<String> maisGanhos){
        List<String> retorno = new ArrayList<>();
        maisAtivos.stream().forEach(x -> {
            if(maisGanhos.contains(x)){
                retorno.add(x);
            }
        });
        return retorno;
    }
}
