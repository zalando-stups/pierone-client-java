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

import org.springframework.web.client.RestOperations;
import org.zalando.stups.fullstop.clients.pierone.PieroneOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link PieroneOperations} with spring.
 *
 * @author  jbellmann
 */
public class RestTemplatePieroneOperations implements PieroneOperations {

    private final RestOperations restOperations;

    private final String baseUrl;

    public RestTemplatePieroneOperations(final RestOperations restOperations, final String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    @Override
    public Map<String, String> listTags(final String team, final String artifact) {
        Map<String, String> uriVariables = new HashMap<>(0);
        uriVariables.put("team", team);
        uriVariables.put("artifact", artifact);

        Map<String, String> result = this.restOperations.getForObject(baseUrl
                    + "/v1/repositories/{team}/{artifact}/tags", Map.class, uriVariables);

        return result;
    }

    @Override
    public Map<String, String> getScmSource(String team, String artifact, String version) {

        Map<String, String> uriVariables = new HashMap<>(0);
        uriVariables.put("team", team);
        uriVariables.put("artifact", artifact);
        uriVariables.put("version", version);

        return this.restOperations.getForObject(baseUrl
                + "/teams/{team}/artifacts/{artifact}/tags/{version}/scm-source", Map.class, uriVariables);
    }
}
