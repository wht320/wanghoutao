package com.wanghoutao.nacos.provider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
@TestPropertySource(properties = {
        "server.port=8082",
        "spring.application.name=nacos-provider"
})
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGreetCaller() throws Exception {
        mockMvc.perform(get("/hello").param("name", "wanghoutao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("nacos-provider"))
                .andExpect(jsonPath("$.port").value(8082))
                .andExpect(jsonPath("$.message").value("hello, wanghoutao"));
    }
}
