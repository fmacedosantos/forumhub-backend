package br.com.forum_hub.infra.seguranca.totp;

import br.com.forum_hub.domain.usuario.Usuario;
import com.atlassian.onetime.service.RandomSecretProvider;
import org.springframework.stereotype.Service;

@Service
public class TotpService {

    public String gerarSecret(){
        return new RandomSecretProvider().generateSecret().getBase32Encoded();
    }

    public String gerarQrCode(Usuario usuario){
        var issuer = "Fórum Hub";

        // Otpauth URL: otpauth://totp/<Issuer>:<User>?secret=<Secret>&issuer=<Issuer>
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, usuario.getUsername(), usuario.getSecret(), issuer
        );
    }
}
