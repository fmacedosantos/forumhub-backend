package br.com.forum_hub.domain.autenticacao.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginGithubService {

    @Value("${FORUM_HUB_CLIENT_ID}")
    private String githubClientId;
    @Value("${FORUM_HUB_CLIENT_SECRET}")
    private String githubClientSecret;
    private final String redirectUri = "http://localhost:8080/login/github/autorizado";
    private final RestClient restClient;

    public LoginGithubService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String gerarUrl(){
        return String.format("https://github.com/login/oauth/authorize" +
                "?client_id=%s" +
                "&redirect_uri=%s" +
                "&scope=read:user,user:email", githubClientId, redirectUri);

    }

    private String obterToken(String code) {
        var resposta = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "code", code,
                        "client_id", githubClientId,
                        "client_secret", githubClientSecret,
                        "redirect_uri", redirectUri
                )).retrieve()
                .body(Map.class);

        return resposta.get("access_token").toString();
    }

    public String obterEmail(String code){
        var token = obterToken(code);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var resposta = restClient.get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DadosEmail[].class);

        for(DadosEmail dadosEmail : resposta){
            if(dadosEmail.primary() && dadosEmail.verified()) {
                return dadosEmail.email();
            }
        }

        return null;
    }
}
