package com.trustai.common_base.api.impl;

import com.trustai.common_base.api.IncomeApi;
import com.trustai.common_base.constants.CommonConstants;
import com.trustai.common_base.dto.IncomeSummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static com.trustai.common_base.utils.RestCallHandler.handleRestCall;

@Service
@Slf4j
public class IncomeApiRestClientImpl implements IncomeApi {
    private final RestClient restClient;

    public IncomeApiRestClientImpl(@Qualifier("v1ApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    public IncomeSummaryDto getIncomeSummary(Long userId) {
        log.info("Calling getIncomeSummary with userId={}", userId);
        return handleRestCall(() ->
                restClient.get()
                        .uri("/api/v1/income/summary")
                        .headers(httpHeaders -> httpHeaders.add(CommonConstants.HEADER_ACTING_USER_ID, userId.toString()))
                        .retrieve()
                        .body(IncomeSummaryDto.class)
        );
    }
}
