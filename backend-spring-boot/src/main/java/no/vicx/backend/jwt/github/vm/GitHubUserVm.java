package no.vicx.backend.jwt.github.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubUserVm(
        String id,
        String login,
        String name,
        String email,
        @JsonProperty("avatar_url")
        String avatarUrl
) {
        public boolean isEmpty(){
                return id == null
                        && login == null
                        && name == null
                        && email == null
                        && avatarUrl == null;
        }
}
