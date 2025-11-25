package br.com.forum_hub.domain.autenticacao.google;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginGoogleService {

    @Value("${google.oauth.clienteId}")
    private String clientId;
    @Value("${google.oauth.clientSecret}")
    private String clientSecret;
    private final String redirectUri = "http://localhost:8080/login/google/autorizado";
    private final RestClient restClient;

    public LoginGoogleService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String gerarUrl(){
        return String.format("https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=%s" +
                "&redirect_uri=%s" +
                "&scope=https://www.googleapis.com/auth/userinfo.email" +
                "&response_type=code" +
                "&access_type=offline", clientId, redirectUri);

    }

    private Map obterTokens(String code) {
        return restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "code", code,
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code"
                )).retrieve()
                .body(Map.class);
    }

    public String obterEmail(String code){
        var tokens = obterTokens(code);

        var idToken = tokens.get("id_token").toString();
        var refreshToken = tokens.get("refresh_token");

        if (refreshToken != null){
            //TODO: guardar no bd
            System.out.println("Refresh token: " + refreshToken);
        }

        var decodedJWT = JWT.decode(idToken);

        return decodedJWT.getClaim("email").asString();
    }


    public String renovarAccessToken(String refreshToken) {
        var resposta = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "refresh_token", refreshToken,
                        "grant_type", "refresh_token"
                )).retrieve()
                .body(Map.class);

        return resposta.get("access_token").toString();

    }
}
