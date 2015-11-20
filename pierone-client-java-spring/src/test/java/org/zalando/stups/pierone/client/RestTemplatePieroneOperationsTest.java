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
package org.zalando.stups.pierone.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.zalando.stups.pierone.client.ResourceUtil.resource;

public class RestTemplatePieroneOperationsTest {

    private final String baseUrl = "http://localhost:8080";

    private MockRestServiceServer mockServer;

    private RestTemplatePieroneOperations client;

    @Before
    public void setUp() {
        final ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();

        final RestTemplate restTemplate = new RestTemplate(singletonList(new MappingJackson2HttpMessageConverter(om)));

        client = new RestTemplatePieroneOperations(restTemplate, baseUrl);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testGetTags() {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags"))
                .andExpect(method(GET))
                .andRespond(withSuccess(resource("/getTags"), APPLICATION_JSON));

        final Map<String, TagSummary> resultMap = client.listTags("testTeam", "testApplication");
        assertThat(resultMap).hasSize(4);
        assertThat(resultMap.get("4.0")).isEqualToComparingFieldByField(
                new TagSummary("4.0", ZonedDateTime.of(2015, 11, 1, 0, 0, 0, 0, ZoneId.of("GMT")), "some_one"));

        mockServer.verify();
    }

    @Test
    public void testGetTagsNotFound() throws Exception {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags"))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));

        assertThat(client.listTags("testTeam", "testApplication")).isEmpty();

        mockServer.verify();
    }

    @Test(expected = HttpServerErrorException.class)
    public void testGetTagsFailed() throws Exception {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags"))
                .andExpect(method(GET))
                .andRespond(withServerError());

        client.listTags("testTeam", "testApplication");
    }

    @Test
    public void getScmSource() {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags/testVersion/scm-source"))
                .andExpect(method(GET))
                .andRespond(withSuccess(resource("/getScmSource"), APPLICATION_JSON));

        Map<String, String> resultMap = client.getScmSource("testTeam", "testApplication", "testVersion");
        assertThat(resultMap).isNotNull();
        assertThat(resultMap).isNotEmpty();
        assertThat(resultMap.size()).isEqualTo(4);
        assertThat(resultMap.get("author")).isEqualTo("hjacobs");

        mockServer.verify();
    }

    @Test
    public void getScmSourceNotFound() {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags/testVersion/scm-source"))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));

        assertThat(client.getScmSource("testTeam", "testApplication", "testVersion")).isNull();

        mockServer.verify();
    }

    @Test(expected = HttpServerErrorException.class)
    public void testGetScmSourceFailed() throws Exception {
        mockServer.expect(requestTo(baseUrl + "/teams/testTeam/artifacts/testApplication/tags/testVersion/scm-source"))
                .andExpect(method(GET))
                .andRespond(withServerError());

        client.getScmSource("testTeam", "testApplication", "testVersion");
    }
}
