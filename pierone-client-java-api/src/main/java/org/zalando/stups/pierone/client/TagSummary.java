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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagSummary {

    private final String name;
    private final ZonedDateTime created;
    private final String createdBy;

    @JsonCreator
    public TagSummary(
            @JsonProperty("name") String name,
            @JsonProperty("created") ZonedDateTime created,
            @JsonProperty("created_by") String createdBy) {
        this.name = name;
        this.created = created;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "TagSummary{" +
                "name='" + name + '\'' +
                ", created=" + created +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
