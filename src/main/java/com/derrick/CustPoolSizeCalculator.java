package com.derrick;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Dre on 2016/12/6.
 */
public class CustPoolSizeCalculator extends PoolSizeCalculator {
    @Override
    protected Runnable creatTask() {
        return new Runnable() {
            @Override
            public void run() {
                String url = "http://localhost:8080/user/tom";
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                requestFactory.setReadTimeout(5000);
                requestFactory.setConnectTimeout(5000);

                // 添加转换器
                List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
                messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

                RestTemplate restTemplate = new RestTemplate(requestFactory);
                restTemplate.setMessageConverters(messageConverters);
                restTemplate.setRequestFactory(requestFactory);
                restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("text", "plain", Charset.forName("UTF-8")));
                headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
                HttpEntity<?> requestEntity = new HttpEntity("", headers);
                ResponseEntity<String> entity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            }
        };
    }

    @Override
    protected BlockingQueue createWorkQueue() {
        return new LinkedBlockingDeque(250);
    }

    @Override
    protected long getCurrentThreadCPUTime() {
        return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    public static void main(String[] args) {
        PoolSizeCalculator poolSizeCalculator = new CustPoolSizeCalculator();
        poolSizeCalculator.calculateBoundaries(new BigDecimal(1.0), new BigDecimal(10000));
    }

}
