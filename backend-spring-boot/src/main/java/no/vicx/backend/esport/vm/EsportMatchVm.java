package no.vicx.backend.esport.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EsportMatchVm(
        long id,
        String name,
        @JsonProperty("begin_at")
        String beginAt,
        String status
) {
}
