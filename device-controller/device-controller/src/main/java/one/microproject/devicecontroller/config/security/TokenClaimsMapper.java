package one.microproject.devicecontroller.config.security;

import one.microproject.iamservice.core.dto.StandardTokenClaims;
import org.springframework.security.core.Authentication;

public interface TokenClaimsMapper {

    /**
     * Maps {@link StandardTokenClaims} data from valid token to Spring {@link Authentication}
     * @param standardTokenClaims - valid {@link StandardTokenClaims} data.
     * @return instance of {@link Authentication}.
     */
    default Authentication map(StandardTokenClaims standardTokenClaims) {
        return new AuthenticationImpl(standardTokenClaims);
    }

}
