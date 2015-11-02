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
package org.zalando.stups.fullstop.clients.pierone;

import java.util.Map;

/**
 * Defines Operations on PierOne.
 */
public interface PieroneOperations {

    /**
     * Returns a mapping between tag names and tag summaries, e.g.
     * <code><pre>
     * {
     *   "1.0": {"name": "1.0", "created": "2015-11-01T00:00:00.000Z", "createdBy": "someone"},
     *   "2.0": {"name": "2.0", "created": "2015-11-02T00:00:00.000Z", "createdBy": "someoneelse"}
     * }
     * </pre></code>
     *
     * @param team     a team id, must not be empty
     * @param artifact an artifact id, muts not be empty
     * @return a Map, never null
     */
    Map<String, TagSummary> listTags(String team, String artifact);

    Map<String, String> getScmSource(String team, String artifact, String version);

}
