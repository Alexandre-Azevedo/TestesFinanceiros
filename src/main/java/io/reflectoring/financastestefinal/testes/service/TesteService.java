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
import java.util.stream.Collector;
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
    public LinkedHashMap<String, String> analiseTemporalDia(List<String> nomesAcoes, boolean analiseInicial){
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
                List<String> horas = new ArrayList<>();
                List<Integer> valoresVolume = new ArrayList<>();
                for(int i =  quartaParte[25].substring(1).equals("-10800]]") ? 26:25; i < quartaParte.length; i += 15){
                    valores.add(Double.valueOf(quartaParte[i].substring(1)));
                    if(quartaParte[i+6].contains("]]")){
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-2)));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
                    }else{
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-1)));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
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
                List<Integer> variacaoVolume = new ArrayList<>();
                AtomicReference<Integer> auxVolume = new AtomicReference<>(null);
                valoresVolume.stream().forEach(x -> {
                    if(auxVolume.get() != null){
                        variacaoVolume.add(((x/ auxVolume.get())-1)*100);
                    }
                    auxVolume.set(x);
                });
                Double valorModa = modaValue(valores).doubleValue();
                Double valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();
                Double variancia = variancia(valores, valorMedia);
                Double isNormal = isNormal(valores, variancia, valorMedia);

                if(!analiseInicial) {
                    /*if((valorModa < valores.get(valores.size() - 1) && valores.get(valores.size() - 1) < valorMedia) ||
                        (valorModa > valores.get(valores.size() - 1) && valores.get(valores.size() - 1) > valorMedia)){
                        System.out.println(nomeAcao+" - possibilidade de venda");
                    }*/
                variacaoMedia.put(nomeAcao, String.format("%.2f", Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " | " +
                        String.format("%.2f", variacaoVolume.stream().mapToDouble(Integer::intValue).sum()/valoresVolume.size()) + " | " +
                        "5D variancia: "+ String.format("%.4f", variancia) + " | " +
                        "5D isNormal: "+ String.format("%.2f", isNormal(valores, variancia, valorMedia)) + "% | " +
                        "5D "+ moda(valores) + " | " +
                        "5D Media: " +  String.format("%.2f", (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size()) + " | " +
                        "5D Atual: " +  valores.get(valores.size() - 1).toString());
            }

            Double menorValorMediaModa = modaValue(valores).doubleValue() <= Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue() ?
                                            modaValue(valores).doubleValue() : Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();

            if( valores.get(valores.size()-1).doubleValue() < menorValorMediaModa){
                if(analiseInicial){
                    variacaoMedia.put(nomeAcao, String.format("%.2f", Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " | " +
                            String.format("%.2f", variacaoVolume.stream().mapToDouble(Integer::intValue).sum()/valoresVolume.size()) + " | " +
                            "5D variancia: "+  String.format("%.4f", variancia) + " | " +
                            "5D isNormal: "+ String.format("%.2f", isNormal(valores, variancia, valorMedia)) + "% | " +
                            "5D "+  moda(valores) + " | " +
                            "5D Media: " +  String.format("%.2f", (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size()) + " | " +
                            "5D Atual: " +  valores.get(valores.size() - 1).toString());
                }
            }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return variacaoMedia;
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

                List<Double> valores = new ArrayList<>();
                List<String> horas = new ArrayList<>();
                List<Integer> valoresVolume = new ArrayList<>();
                for(int i =  quartaParte[25].substring(1).equals("-10800]]") ? 26:25; i < quartaParte.length; i += 15){
                    valores.add(Double.valueOf(quartaParte[i].substring(1)));
                    if(quartaParte[i+6].contains("]]")){
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-2)));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
                    }else{
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-1)));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
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
                List<Integer> variacaoVolume = new ArrayList<>();
                AtomicReference<Integer> auxVolume = new AtomicReference<>(null);
                valoresVolume.stream().forEach(x -> {
                    if(auxVolume.get() != null){
                        variacaoVolume.add(((x/ auxVolume.get())-1)*100);
                    }
                    auxVolume.set(x);
                });
                Double valorModa = modaValue(valores).doubleValue();
                Double valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();
                Double variancia = variancia(valores, valorMedia);
                Double isNormal = isNormal(valores, variancia, valorMedia);

                if(!analiseInicial) {
                    /*if((valorModa < valores.get(valores.size() - 1) && valores.get(valores.size() - 1) < valorMedia) ||
                        (valorModa > valores.get(valores.size() - 1) && valores.get(valores.size() - 1) > valorMedia)){
                        System.out.println(nomeAcao+" - possibilidade de venda");
                    }*/
                    variacaoMedia.put(nomeAcao, horas.get(horas.size()-1)+" | "+
                            "5D Atual: " +  valores.get(valores.size() - 1).toString()+" | "+
                            headShoulderPattern(valores)+ " | "+
                            headShoulderReversePattern(valores)+" | "+
                            cunhaDeBaixaPattern(valores)+" | "+
                            trinaguloDeReversaoPattern(valores)+" | "+
                            cestoBasePattern(valores));
                }

            Double menorValorMediaModa = modaValue(valores).doubleValue() <= Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue() ?
                                            modaValue(valores).doubleValue() : Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();

            if( valores.get(valores.size()-1).doubleValue() < menorValorMediaModa){
                if(analiseInicial){
                    variacaoMedia.put(nomeAcao, horas.get(horas.size()-1)+" | "+
                            "5D Atual: " +  valores.get(valores.size() - 1).toString()+" | "+
                            headShoulderPattern(valores)+ " | "+
                            headShoulderReversePattern(valores)+" | "+
                            cunhaDeBaixaPattern(valores)+" | "+
                            trinaguloDeReversaoPattern(valores)+" | "+
                            cestoBasePattern(valores));
                }
            }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return variacaoMedia;
    }
    public LinkedHashMap<String, String> analiseTemporalMes(List<String> nomesAcoes, boolean analiseInicial){
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
                String[] primeiraParte = resposta.split("key: 'ds:11'");
                String[] segundaParte = primeiraParte[1].split("sideChannel");
                String terceiraParte = segundaParte[0].substring(60, segundaParte[0].length()-2);
                String[] quartaParte = terceiraParte.split(",");

                List<Double> valores = new ArrayList<>();
                List<String> horas = new ArrayList<>();
                List<Integer> valoresVolume = new ArrayList<>();
                for(int i =  quartaParte[8].substring(1).equals("-10800]]") ? 9:10; i < quartaParte.length; i += 15){
                    valores.add(Double.valueOf(quartaParte[i].substring(1)));
                    if(quartaParte[i+6].contains("[]]")){
                        valoresVolume.add(Integer.valueOf("0"));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
                    } else if(quartaParte[i+6].contains("]]")){
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-2)));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
                    }else{
                        valoresVolume.add(Integer.valueOf(quartaParte[i+6].substring(0,quartaParte[i+6].length()-1)));
                        horas.add(quartaParte[i-5]+":"+quartaParte[i-4]);
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
                List<Integer> variacaoVolume = new ArrayList<>();
                AtomicReference<Integer> auxVolume = new AtomicReference<>(null);
                valoresVolume.stream().forEach(x -> {
                    if(auxVolume.get() != null){
                        variacaoVolume.add(((x/ auxVolume.get())-1)*100);
                    }
                    auxVolume.set(x);
                });
                Double valorModa = modaValue(valores).doubleValue();
                Double valorMedia = Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();
                Double variancia = variancia(valores, valorMedia);
                Double isNormal = isNormal(valores, variancia, valorMedia);

                if(!analiseInicial) {
                    /*if((valorModa < valores.get(valores.size() - 1) && valores.get(valores.size() - 1) < valorMedia) ||
                        (valorModa > valores.get(valores.size() - 1) && valores.get(valores.size() - 1) > valorMedia)){
                        System.out.println(nomeAcao+" - possibilidade de venda");
                    }*/
                variacaoMedia.put(nomeAcao, String.format("%.2f", Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " | " +
                        String.format("%.2f", variacaoVolume.stream().mapToDouble(Integer::intValue).sum()/valoresVolume.size()) + " | " +
                        "5D variancia: "+ String.format("%.4f", variancia) + " | " +
                        "5D isNormal: "+ String.format("%.2f", isNormal(valores, variancia, valorMedia)) + "% | " +
                        "5D "+ moda(valores) + " | " +
                        "5D Media: " +  String.format("%.2f", (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size()) + " | " +
                        "5D Atual: " +  valores.get(valores.size() - 1).toString()+"\n"+
                        horas.get(horas.size()-1)+" | "+headShoulderPattern(valores)+ " | "+
                        headShoulderReversePattern(valores)+" | "+
                        cunhaDeBaixaPattern(valores)+" | "+
                        trinaguloDeReversaoPattern(valores)+" | "+
                        cestoBasePattern(valores));
                }

            Double menorValorMediaModa = modaValue(valores).doubleValue() <= Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue() ?
                                            modaValue(valores).doubleValue() : Double.valueOf(valores.stream().mapToDouble(Double::doubleValue).sum()/valores.size()).doubleValue();

            if( valores.get(valores.size()-1).doubleValue() < menorValorMediaModa){
                if(analiseInicial){
                    variacaoMedia.put(nomeAcao, String.format("%.2f", Double.valueOf(variacao.stream().mapToDouble(Double::doubleValue).sum() / variacao.size()) * 100) + " | " +
                            String.format("%.2f", variacaoVolume.stream().mapToDouble(Integer::intValue).sum()/valoresVolume.size()) + " | " +
                            "5D variancia: "+  String.format("%.4f", variancia) + " | " +
                            "5D isNormal: "+ String.format("%.2f", isNormal(valores, variancia, valorMedia)) + "% | " +
                            "5D "+  moda(valores) + " | " +
                            "5D Media: " +  String.format("%.2f", (valores.stream().mapToDouble(Double::doubleValue).sum()) / valores.size()) + " | " +
                            "5D Atual: " +  valores.get(valores.size() - 1).toString()+"\n"+
                            horas.get(horas.size()-1)+" | "+headShoulderPattern(valores)+ " | "+
                            headShoulderReversePattern(valores)+" | "+
                            cunhaDeBaixaPattern(valores)+" | "+
                            trinaguloDeReversaoPattern(valores)+" | "+
                            cestoBasePattern(valores));
                }
            }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return variacaoMedia;
    }

    public String moda(List<Double> valores){
        String retorno = "Moda: ";
        AtomicReference<Integer> quantidade = new AtomicReference<>(0);
        AtomicReference<Double> moda = new AtomicReference<>(0.0d);
        valores.stream().forEach(x -> {
            Integer quantidadeValor = (valores.stream().filter(y -> y.equals(x)).collect(Collectors.toList())).size();
            quantidade.set(quantidade.get() > quantidadeValor ? quantidade.get() : quantidadeValor);
            moda.set(quantidade.get() > quantidadeValor ? moda.get() : x);
        });
        retorno += moda.get().toString() + "("+String.format("%.2f", (Double.valueOf(quantidade.get()).doubleValue()/Double.valueOf(valores.size()).doubleValue())*100)+"%)";
        return retorno;
    }

    public Double modaValue(List<Double> valores){
        AtomicReference<Integer> quantidade = new AtomicReference<>(0);
        AtomicReference<Double> moda = new AtomicReference<>(0.0d);
        valores.stream().forEach(x -> {
            Integer quantidadeValor = (valores.stream().filter(y -> y.equals(x)).collect(Collectors.toList())).size();
            quantidade.set(quantidade.get() > quantidadeValor ? quantidade.get() : quantidadeValor);
            moda.set(quantidade.get() > quantidadeValor ? moda.get() : x);
        });
        return moda.get();
    }

    public Double variancia(List<Double> valores, Double media){
        AtomicReference<Double> valorMenosMediaAcumuladoSqt = new AtomicReference<>(0d);
        valores.stream().forEach(x -> {
            valorMenosMediaAcumuladoSqt.set(Math.pow(x - media, 2));
        });
        return Math.sqrt(valorMenosMediaAcumuladoSqt.get()/valores.size());
    }
    
    public Double isNormal(List<Double> valores, Double variancia, Double media){
        List<Double> valoresDentroDaNormal = valores.stream().filter(x -> x > media - variancia && x < media + variancia).collect((Collectors.toList()));
        return (Double.valueOf(valoresDentroDaNormal.size()).doubleValue()/Double.valueOf(valores.size()).doubleValue())*100;
    }


    public String headShoulderPattern(List<Double> valores){
        int i = valores.size() -1;
        int aux = 1;
        while(i - 5*aux > 0){
            if((valores.get(i) > valores.get(i-1*aux)) && (valores.get(i-1*aux) < valores.get(i-2*aux)) && (valores.get(i-2*aux) > valores.get(i-3*aux)) && (valores.get(i-3*aux) < valores.get(i-4*aux)) && (valores.get(i-4*aux) > valores.get(i-5*aux)) &&
                    (new BigDecimal(valores.get(i-2*aux) - valores.get(i-4*aux)).setScale(1, RoundingMode.HALF_EVEN)).doubleValue() == 0.0){
                return "HS - BAIXA("+aux+")";
            }
            aux++;
        }

        return "HS - NÃO ENCONTRADO";
    }
    public String headShoulderReversePattern(List<Double> valores){
        int i = valores.size() -1;
        int aux = 1;
        while(i - 5*aux > 0){
            if((valores.get(i) < valores.get(i-1*aux)) && (valores.get(i-1*aux) > valores.get(i-2*aux)) && (valores.get(i-2*aux) < valores.get(i-3*aux)) && (valores.get(i-3*aux) > valores.get(i-4*aux)) && (valores.get(i-4*aux) < valores.get(i-5*aux)) &&
                    (new BigDecimal(valores.get(i-2*aux) - valores.get(i-4*aux)).setScale(1, RoundingMode.HALF_EVEN)).doubleValue() == 0.0){
                return "HSR - ALTA("+aux+")";
            }
            aux++;
        }

        return "HSR - NÃO ENCONTRADO";
    }
    public String cunhaDeBaixaPattern(List<Double> valores){
        int i = valores.size() -1;
        int aux = 1;
        while(i - 9*aux > 0){
            if((valores.get(i) > valores.get(i-1*aux)) && (valores.get(i-1*aux) < valores.get(i-2*aux)) && (valores.get(i-2*aux) > valores.get(i-3*aux)) && (valores.get(i-3*aux) < valores.get(i-4*aux)) && (valores.get(i-4*aux) > valores.get(i-5*aux)) &&
                    (valores.get(i-5*aux) < valores.get(i-6*aux)) && (valores.get(i-6*aux) > valores.get(i-7*aux)) && (valores.get(i-7*aux) < valores.get(i-8*aux))
                    && ((valores.get(i) < valores.get(i-2*aux)) && (valores.get(i-2*aux) < valores.get(i-4*aux)) && (valores.get(i-4*aux) < valores.get(i-6*aux)) && (valores.get(i-6*aux) < valores.get(i-8*aux)))){
                return "CUNHA DE BAIXA - ALTA("+aux+")";
            }
            aux++;
        }

        return "CUNHA DE BAIXA - NÃO ENCONTRADO";
    }

    public String trinaguloDeReversaoPattern(List<Double> valores){
        int i = valores.size() -1;
        int aux = 1;
        while(i - 9*aux > 0){
            if((valores.get(i) < valores.get(i-1*aux)) && (valores.get(i-1*aux) > valores.get(i-2*aux)) && (valores.get(i-2*aux) < valores.get(i-3*aux)) && (valores.get(i-3*aux) > valores.get(i-4*aux)) && (valores.get(i-4*aux) < valores.get(i-5*aux)) &&
                    (valores.get(i-5*aux) > valores.get(i-6*aux)) && (valores.get(i-6*aux) < valores.get(i-7*aux)) && (valores.get(i-7*aux) > valores.get(i-8*aux))
                    && ((valores.get(i) > valores.get(i-2*aux)) && (valores.get(i-2*aux) > valores.get(i-4*aux)) && (valores.get(i-4*aux) > valores.get(i-6*aux)) && (valores.get(i-6*aux) > valores.get(i-8*aux)))){
                return "TRI. REV. - BAIXA("+aux+")";
            }
            aux++;
        }

        return "TRI. REV. - NÃO ENCONTRADO";
    }
    public String cestoBasePattern(List<Double> valores){
        int i = valores.size() -1;
        Double valorAtual = valores.get(i);
        Integer indexOcorrenciaAnterior = null;
        int quantidadeElementos = 1;
        for(int j = i-1; j>-1; j--){
            quantidadeElementos++;
            if(valores.get(j).equals(valorAtual)){
                indexOcorrenciaAnterior = j;
            }
        }
        if(quantidadeElementos != 1 && indexOcorrenciaAnterior != null){
            int aux = 1;
            while (aux < quantidadeElementos-1){
                if(quantidadeElementos%aux == 0){
                    boolean maiorMenor = false;
                    for(int j = indexOcorrenciaAnterior; j < valores.size() -1; j += 2){
                        if(valores.get(j) < valores.get(j+1)){
                            maiorMenor = true;
                        }
                    }
                    if(maiorMenor){
                        return "CESTO BASE - ALTA("+indexOcorrenciaAnterior+","+aux+","+(valores.size() -1)+")";
                    }
                    aux++;
                }
                aux = 1;
            }
        }

        return "CESTO BASE - NÃO ENCONTRADO";
    }


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
