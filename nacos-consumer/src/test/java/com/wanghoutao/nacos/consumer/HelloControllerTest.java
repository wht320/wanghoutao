package com.wanghoutao.nacos.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderClient providerClient;

    @Test
    void shouldDelegateToProvider() throws Exception {
        Map<String, Object> providerBody = new LinkedHashMap<>();
        providerBody.put("service", "nacos-provider");
        providerBody.put("port", 8082);
        providerBody.put("message", "hello, nacos");
        when(providerClient.hello("nacos")).thenReturn(providerBody);

        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("nacos-provider"))
                .andExpect(jsonPath("$.message").value("hello, nacos"));
    }
}
