package org.library.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ServiceUnavailableException;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.library.dto.openlibrary.OpenLibraryResponse;

import java.util.List;

@RegisterRestClient(configKey = "open-library")
@Path("/search.json")
public interface OpenLibraryClient {

    @GET
    @Retry(maxRetries = 3)
    @Timeout(5000)
    @Fallback(fallbackMethod = "fallbackSearchByTitle")
    Uni<OpenLibraryResponse> searchByTitle(
            @QueryParam("title") String title,
            @QueryParam("fields") List<String> fields,
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit);

    default Uni<OpenLibraryResponse> fallbackSearchByTitle(String title, List<String> fields, Integer offset, Integer limit) {
        return Uni.createFrom().failure(new ServiceUnavailableException("Open Library service is unavailable. Please try again later.", 15L));
    }
}
