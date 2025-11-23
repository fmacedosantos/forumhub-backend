package br.com.forum_hub.domain.autenticacao.github;

import org.springframework.beans.factory.annotation.Value;
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

    public String obterToken(String code) {
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
                .body(String.class);

        return resposta;
    }
}
