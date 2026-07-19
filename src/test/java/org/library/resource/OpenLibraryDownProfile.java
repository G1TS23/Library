package org.library.resource;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class OpenLibraryDownProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides(){
        return Map.of("quarkus.rest-client.open-library.url", "http://localhost:1");
    }
}
