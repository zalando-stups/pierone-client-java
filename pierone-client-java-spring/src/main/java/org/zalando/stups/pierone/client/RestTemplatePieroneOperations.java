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


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;
import static org.zalando.stups.pierone.client.RequestHelper.call;

/**
 * Implementation of {@link PieroneOperations} with spring.
 */
public class RestTemplatePieroneOperations implements PieroneOperations {

    private static final ParameterizedTypeReference<List<TagSummary>> LIST_OF_TAG_SUMMARIES = new ParameterizedTypeReference<List<TagSummary>>() {
    };
    private static final ParameterizedTypeReference<Map<String, String>> MAP_STRING_TO_STRING = new ParameterizedTypeReference<Map<String, String>>() {
    };

    private final RestOperations restOperations;

    private final String baseUrl;

    public RestTemplatePieroneOperations(final RestOperations restOperations, final String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    @Override
    public Map<String, TagSummary> listTags(final String team, final String artifact) {
        final URI uri = fromHttpUrl(baseUrl + "/teams/{team}/artifacts/{artifact}/tags").buildAndExpand(team, artifact).toUri();
        final List<TagSummary> tags =
                call(() -> restOperations.exchange(get(uri).build(), LIST_OF_TAG_SUMMARIES).getBody())
                        .returnOn(NOT_FOUND, Collections::emptyList)
                        .perform();
        final Map<String, TagSummary> result = new LinkedHashMap<>(tags.size());
        tags.forEach((tag) -> result.put(tag.getName(), tag));
        return result;
    }

    @Override
    public Map<String, String> getScmSource(String team, String artifact, String version) {
        final URI uri = fromHttpUrl(baseUrl + "/teams/{team}/artifacts/{artifact}/tags/{version}/scm-source").buildAndExpand(team, artifact, version).toUri();
        return call(() -> this.restOperations.exchange(get(uri).build(), MAP_STRING_TO_STRING).getBody())
                .returnOn(NOT_FOUND, () -> null)
                .perform();
    }

}
