package com.mytoon.backend.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record GoogleTokenResponse(
        @JsonProperty("access_token") String accessToken
) {
}
