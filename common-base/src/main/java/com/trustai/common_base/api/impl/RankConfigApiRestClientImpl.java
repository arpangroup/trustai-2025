package com.trustai.common_base.api.impl;

import com.trustai.common_base.api.RankConfigApi;
import com.trustai.common_base.dto.RankConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

import static com.trustai.common_base.utils.RestCallHandler.handleRestCall;

@Service
@Primary
@Slf4j
public class RankConfigApiRestClientImpl implements RankConfigApi {
    private final RestClient restClient;

    public RankConfigApiRestClientImpl(
            @Qualifier("v1ApiRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    @Override
    public List<RankConfigDto> getAllRanks() {
        return handleRestCall(() -> {
            RankConfigDto[] ranks = restClient.get()
                    .uri("/rankings")
                    .retrieve()
                    //.body(new ParameterizedTypeReference<List<UserInfo>>() {});
                    .body(RankConfigDto[].class);
            return Arrays.asList(ranks);
        });
    }

    @Override
    public RankConfigDto getRankConfigByRankCode(String rankCode) {
        log.info("Calling getRankConfigByRankCode with rankCode={}", rankCode);

        return handleRestCall(() ->
                restClient.get()
                        .uri("/rankings/code/{rankCode}", rankCode)
                        .retrieve()
                        .body(RankConfigDto.class)
        );
    }
}
