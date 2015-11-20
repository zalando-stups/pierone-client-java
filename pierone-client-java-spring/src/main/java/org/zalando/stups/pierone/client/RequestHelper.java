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

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

class RequestHelper<T> {
    private final Supplier<T> request;
    private final Map<HttpStatus, Supplier<T>> errorHandlers = new HashMap<>();

    private RequestHelper(Supplier<T> request) {
        this.request = request;
    }

    public static <T> RequestHelper<T> call(final Supplier<T> request) {
        return new RequestHelper<>(request);
    }

    public RequestHelper<T> returnOn(final HttpStatus status, final Supplier<T> supplier) {
        errorHandlers.put(status, supplier);
        return this;
    }

    public T perform() {
        try {
            return request.get();
        } catch (HttpStatusCodeException e) {
            return errorHandlers.getOrDefault(e.getStatusCode(), reThrow(e)).get();
        }
    }

    private static <T> Supplier<T> reThrow(final RuntimeException e) {
        return () -> {
            throw e;
        };
    }
}
