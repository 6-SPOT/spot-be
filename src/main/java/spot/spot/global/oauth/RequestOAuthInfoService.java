package spot.spot.global.oauth;

import org.springframework.stereotype.Component;
import spot.spot.domain.member.OAuthProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {
    private final Map<OAuthProvider,OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients){
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity()
                ));
        // clients는 OAuthProvider를 키로, OAuthApiClient 객체를 값으로 가지는 불변 map
    }

    public OAuthInfoResponse request(OAuthLoginParams params){
        OAuthApiClient client = clients.get(params.oAuthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestOauthInfo(accessToken);
    }
}