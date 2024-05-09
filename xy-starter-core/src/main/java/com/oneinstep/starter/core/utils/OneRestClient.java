package com.oneinstep.starter.core.utils;

import com.alibaba.fastjson2.JSON;
import com.oneinstep.starter.core.exception.HttpCallException;
import com.oneinstep.starter.core.log.annotition.Logging;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author : hahaha
 * @created : 2023/9/22
 * @since: 1.0.0
 **/
@Component
@Slf4j
public class OneRestClient {

    @Resource
    private RestTemplate restTemplate;

    public String makePostRequest(String url, String requestBody, Map<String, Object> paramMap, HttpContentType type) {
        return makePostRequest(url, requestBody, paramMap, type, null);
    }

    /**
     * Make a GET request to the given URL
     *
     * @param url         The URL to make the request to
     * @param requestBody The request body
     * @return The response body as a String
     */
    @Logging(printArgs = true, printResult = true)
    public String makePostRequest(String url, String requestBody, Map<String, Object> paramMap, HttpContentType type, HttpHeaders headers) {

        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url is blank");
        }

        // Create headers with JSON content type
        log.info("makePostRequest >>>>>>>>>>> url: {}, requestBody: {}, type={}, paramMap={}", url, requestBody, type, JSON.toJSONString(paramMap));

        if (headers == null) {
            headers = new HttpHeaders();
        }

        String response;
        try {

            if (type == HttpContentType.JSON) {
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            } else if (type == HttpContentType.FORM) {
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                // put all paramMap into params
                if (MapUtils.isNotEmpty(paramMap)) {
                    paramMap.forEach(params::add);
                }
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
                response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            } else if (type == HttpContentType.FORM_DATA) {
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                // put all paramMap into params
                if (MapUtils.isNotEmpty(paramMap)) {
                    paramMap.forEach((k, v) -> params.add(k, String.valueOf(v)));
                }
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
                response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            } else {
                throw new IllegalArgumentException("HttpContentType is not supported");
            }

        } catch (HttpClientErrorException ce) {
            log.error("makePostRequest <<<<<<<<<< error", ce);
            return ce.getResponseBodyAs(String.class);
        } catch (Exception e) {
            log.error("makePostRequest <<<<<<<<<< error", e);
            throw new HttpCallException();
        }

        // 打印 500 个字符
        if (response != null && response.length() > 500) {
            log.info("makePostRequest <<<<<<<<<< response: {}", response.substring(0, 500));
        } else {
            log.info("makePostRequest <<<<<<<<<< response: {}", response);
        }

        return response;
    }

    public String makeGetRequest(String url, Map<String, Object> paramMap) throws HttpCallException {
        return makeGetRequest(url, paramMap, null, false);
    }

    public String makeGetRequest(String url, Map<String, Object> paramMap, boolean urlEncode) throws HttpCallException {
        return makeGetRequest(url, paramMap, null, urlEncode);
    }

    public String makeGetRequest(String url, Map<String, Object> paramMap, HttpHeaders headers) {
        return makeGetRequest(url, paramMap, headers, false);
    }

    @Logging(printArgs = true, printResult = true)
    public String makeGetRequest(String url, Map<String, Object> paramMap, HttpHeaders headers, boolean urlEncode) throws HttpCallException {

        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url is blank");
        }

        // Create headers with JSON content type
        log.info("makeGetRequest >>>>>>>>>>> url: {}, paramMap={}", url, JSON.toJSONString(paramMap));

        String response;
        try {

            // 将 参数拼接到 url 后面
            if (MapUtils.isNotEmpty(paramMap)) {
                StringBuilder sb = new StringBuilder();
                paramMap.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));
                url = url + "?" + sb.substring(0, sb.length() - 1);
            }

            if (headers == null) {
                headers = new HttpHeaders();
            }
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            log.info("the final url is: {}", url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode.is2xxSuccessful()) {
                response = responseEntity.getBody();
            } else {
                log.error("makeGetRequest <<<<<<<<<< error, statusCode: {}", statusCode);

                response = responseEntity.getBody();
                log.error("makeGetRequest <<<<<<<<<< error, body: {}", response);
            }

        } catch (HttpClientErrorException ce) {
            log.error("makeGetRequest <<<<<<<<<< error", ce);
            return ce.getResponseBodyAs(String.class);
        } catch (Exception e) {
            log.error("makeGetRequest <<<<<<<<<< error", e);
            throw new HttpCallException();
        }

        // 打印 500 个字符
        if (response != null && response.length() > 500) {
            log.info("makeGetRequest <<<<<<<<<< response: {}", response.substring(0, 500));
        } else {
            log.info("makeGetRequest <<<<<<<<<< response: {}", response);
        }

        return response;
    }

}
