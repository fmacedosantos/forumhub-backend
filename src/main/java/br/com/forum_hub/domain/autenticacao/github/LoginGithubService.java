package br.com.forum_hub.domain.autenticacao.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginGithubService {

    @Value("${FORUM_HUB_CLIENT_ID}")
    private String githubClientId;

    public String gerarUrl(){
        return String.format("https://github.com/login/oauth/authorize" +
                "?client_id=%s" +
                "&redirect_uri=http://localhost:8080/login/github/autorizado" +
                "&scope=read:user,user:email", githubClientId);

    }
}
