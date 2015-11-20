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

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.util.Maps.newHashMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HystrixSpringPieroneOperationsTest {

    private static final String TEAM_ID = "foo";

    private static final String ARTIFACT_ID = "bar";

    private static final String VERSION_ID = "hello";

    private static final Map<String, TagSummary> TAGS = newHashMap();

    private static final Map<String, String> SCM_SOURCE = newHashMap();

    @Autowired
    private PieroneOperations mockPieroneOperations;

    @Autowired
    private PieroneOperations hystrixSpringPieroneOperations;

    @Before
    public void setUp() throws Exception {
        reset(mockPieroneOperations);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(mockPieroneOperations);
    }

    @Test
    public void testListTags() throws Exception {
        when(mockPieroneOperations.listTags(anyString(), anyString())).thenReturn(TAGS);
        assertThat(hystrixSpringPieroneOperations.listTags(TEAM_ID, ARTIFACT_ID)).isSameAs(TAGS);
        verify(mockPieroneOperations).listTags(same(TEAM_ID), same(ARTIFACT_ID));
    }

    @Test
    public void testListTagsServerError() throws Exception {
        final HttpServerErrorException serverError = new HttpServerErrorException(SERVICE_UNAVAILABLE);

        when(mockPieroneOperations.listTags(anyString(), anyString())).thenThrow(serverError);

        try {
            hystrixSpringPieroneOperations.listTags(TEAM_ID, ARTIFACT_ID);
            failBecauseExceptionWasNotThrown(HystrixRuntimeException.class);
        } catch (HystrixRuntimeException e) {
            assertThat(e).hasCause(serverError);
        }

        verify(mockPieroneOperations).listTags(same(TEAM_ID), same(ARTIFACT_ID));
    }

    @Test
    public void testListTagsClientError() throws Exception {
        final HttpClientErrorException clientError = new HttpClientErrorException(BAD_REQUEST);

        when(mockPieroneOperations.listTags(anyString(), anyString())).thenThrow(clientError);

        try {
            hystrixSpringPieroneOperations.listTags(TEAM_ID, ARTIFACT_ID);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException ignored) {
        }

        verify(mockPieroneOperations).listTags(same(TEAM_ID), same(ARTIFACT_ID));
    }

    @Test
    public void testGetScmSource() throws Exception {
        when(mockPieroneOperations.getScmSource(anyString(), anyString(), anyString())).thenReturn(SCM_SOURCE);
        assertThat(hystrixSpringPieroneOperations.getScmSource(TEAM_ID, ARTIFACT_ID, VERSION_ID)).isSameAs(SCM_SOURCE);
        verify(mockPieroneOperations).getScmSource(same(TEAM_ID), same(ARTIFACT_ID), same(VERSION_ID));
    }

    @Test
    public void testGetScmSourceServerError() throws Exception {
        final HttpServerErrorException serverError = new HttpServerErrorException(SERVICE_UNAVAILABLE);

        when(mockPieroneOperations.getScmSource(anyString(), anyString(), anyString())).thenThrow(serverError);

        try {
            hystrixSpringPieroneOperations.getScmSource(TEAM_ID, ARTIFACT_ID, VERSION_ID);
            failBecauseExceptionWasNotThrown(HystrixRuntimeException.class);
        } catch (HystrixRuntimeException e) {
            assertThat(e).hasCause(serverError);
        }

        verify(mockPieroneOperations).getScmSource(same(TEAM_ID), same(ARTIFACT_ID), same(VERSION_ID));
    }

    @Test
    public void testGetScmSourceClientError() throws Exception {
        final HttpClientErrorException clientError = new HttpClientErrorException(BAD_REQUEST);

        when(mockPieroneOperations.getScmSource(anyString(), anyString(), anyString())).thenThrow(clientError);

        try {
            hystrixSpringPieroneOperations.getScmSource(TEAM_ID, ARTIFACT_ID, VERSION_ID);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException ignored) {
        }

        verify(mockPieroneOperations).getScmSource(same(TEAM_ID), same(ARTIFACT_ID), same(VERSION_ID));
    }

    @Configuration
    @EnableHystrix
    @EnableAspectJAutoProxy
    static class TestConfig {

        @Bean
        PieroneOperations hystrixSpringPieroneOperations(PieroneOperations mockPieroneOperations) {
            return new HystrixSpringPieroneOperations(mockPieroneOperations);
        }

        @Bean
        PieroneOperations mockPieroneOperations() {
            return mock(PieroneOperations.class);
        }
    }
}
