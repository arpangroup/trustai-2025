/*
package com.trustai.income_service.client;

import com.trustai.common_base.api.UserApi;
import com.trustai.common_base.config.FeignConfig;
import com.trustai.common_base.constants.CommonConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = CommonConstants.USER_SERVICE,
        url = CommonConstants.BASE_URL,
        path = CommonConstants.PATH_USER_SERVICE,
        configuration = FeignConfig.class
)
@ConditionalOnProperty(name = "feign.client.enabled.income", havingValue = "true")
public interface IncomeUserClient extends UserApi {
}
*/
