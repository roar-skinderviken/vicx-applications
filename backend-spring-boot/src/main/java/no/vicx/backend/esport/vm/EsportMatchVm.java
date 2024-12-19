package no.vicx.backend.esport.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EsportMatchVm(
        @JsonProperty("begin_at")
        String beginAt,
        String status,
        List<OpponentVm> opponents
) {
}
