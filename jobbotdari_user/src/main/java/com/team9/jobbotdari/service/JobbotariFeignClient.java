package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.AddCompanyRequestDto;
import com.team9.jobbotdari.dto.response.BaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("jobbotdari")
public interface JobbotariFeignClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/company", consumes = "application/json")
    BaseResponseDto addCompany(@RequestBody AddCompanyRequestDto addCompanyRequestDto);
}
