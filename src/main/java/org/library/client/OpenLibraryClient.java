package org.library.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.library.dto.openlibrary.OpenLibraryResponse;

import java.util.List;

@RegisterRestClient(configKey = "open-library")
@Path("/search.json")
public interface OpenLibraryClient {

    @GET
    OpenLibraryResponse searchByTitle(
            @QueryParam("title") String title,
            @QueryParam("fields") List<String> fields,
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit);
}
