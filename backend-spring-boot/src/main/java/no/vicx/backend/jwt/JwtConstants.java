package no.vicx.backend.jwt;

public final class JwtConstants {
    private JwtConstants() {
    }

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_IMAGE = "image";
    public static final String CLAIM_SCOPES = "scopes";
    public static final String CLAIM_ROLES = "roles";

    public static final String HEADER_ALG = "alg";
    public static final String HEADER_ALG_NONE = "none";
}
