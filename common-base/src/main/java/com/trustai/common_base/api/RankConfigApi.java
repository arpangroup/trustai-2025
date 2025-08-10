package com.trustai.common_base.api;

import com.trustai.common_base.dto.RankConfigDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RankConfigApi {//code/{rankCode}
    @GetMapping("/rankings")
    List<RankConfigDto> getAllRanks();

    @GetMapping("/rankings/code/{rankCode}")
    RankConfigDto getRankConfigByRankCode(@PathVariable String rankCode);

}
