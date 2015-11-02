/**
 * Copyright (C) 2015 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zalando.stups.fullstop.clients.pierone.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.test.web.client.MockRestServiceServer;
import org.zalando.stups.fullstop.clients.pierone.TagSummary;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.zalando.stups.fullstop.clients.pierone.spring.ResourceUtil.resource;

public class RestTemplatePieroneOperationsTest {

    private final String baseUrl = "http://localhost:8080";

    private MockRestServiceServer mockServer;

    private RestTemplatePieroneOperations client;

    @Before
    public void setUp() {

        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        resource.setClientId("what_here");

        final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);
        restTemplate.setAccessTokenProvider(new TestAccessTokenProvider("86c45354-8bc4-44bf-905f-5f34ebe0b599"));
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        final ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        restTemplate.setMessageConverters(singletonList(new MappingJackson2HttpMessageConverter(om)));

        client = new RestTemplatePieroneOperations(restTemplate, baseUrl);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    //J-
    @Test
    public void getTags() {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags"))
                .andExpect(method(GET))
                .andExpect(header("Authorization", "Bearer 86c45354-8bc4-44bf-905f-5f34ebe0b599"))
                .andRespond(withSuccess(resource("/getTags"), APPLICATION_JSON));

        final Map<String, TagSummary> resultMap = client.listTags("testTeam", "testApplication");
        assertThat(resultMap).hasSize(4);
        final TagSummary tag = resultMap.get("4.0");
        assertThat(tag.getName()).isEqualTo("4.0");
        assertThat(tag.getCreated().toEpochSecond()).isEqualTo(ZonedDateTime.of(2015, 11, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toEpochSecond());
        assertThat(tag.getCreatedBy()).isEqualTo("some_one");

        mockServer.verify();
    }
    //J+

    @Test
    public void getScmSource() {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags/testVersion/scm-source"))
                .andExpect(method(GET))
                .andExpect(header("Authorization", "Bearer 86c45354-8bc4-44bf-905f-5f34ebe0b599"))
                .andRespond(withSuccess(resource("/getScmSource"), APPLICATION_JSON));

        Map<String, String> resultMap = client.getScmSource("testTeam", "testApplication", "testVersion");
        assertThat(resultMap).isNotNull();
        assertThat(resultMap).isNotEmpty();
        assertThat(resultMap.size()).isEqualTo(4);
        assertThat(resultMap.get("author")).isEqualTo("hjacobs");

        mockServer.verify();
    }
}
