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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

public class HystrixSpringPieroneOperations implements PieroneOperations {

    private final PieroneOperations delegate;

    public HystrixSpringPieroneOperations(PieroneOperations delegate) {
        Assert.notNull(delegate, "delegate must not be null");
        this.delegate = delegate;
    }

    @Override
    @HystrixCommand(ignoreExceptions = HttpClientErrorException.class)
    public Map<String, TagSummary> listTags(String team, String artifact) {
        return delegate.listTags(team, artifact);
    }

    @Override
    @HystrixCommand(ignoreExceptions = HttpClientErrorException.class)
    public Map<String, String> getScmSource(String team, String artifact, String version) {
        return delegate.getScmSource(team, artifact, version);
    }
}
