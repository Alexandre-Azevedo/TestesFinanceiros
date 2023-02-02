package io.reflectoring.financastestefinal.testes.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TesteService {
    public void PegarConteudoPelaClasse(String html, String nomeClasse){
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
        System.out.println(retorno);
    }
}
