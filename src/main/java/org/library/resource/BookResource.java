package org.library.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.library.entity.Book;

@Path("/books")
public class BookResource {

    @GET
    public String books() {
        return "Books";
    }

    @GET
    @Path("/{id}")
    public String book(@PathParam("id") int id) {
        return "Book " + id;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(Book book) {
        return Response.status(Response.Status.CREATED)
                .entity(book)
                .build();
    }

}
