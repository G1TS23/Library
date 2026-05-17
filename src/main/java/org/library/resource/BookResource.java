package org.library.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.service.BookService;
import java.util.List;

/**
 * JAX-RS resource exposing REST endpoints for book management.
 */
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Gestion des livres")
public class BookResource {

    private final BookService bookService;

    @Inject
    public BookResource(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Retrieves the list of all books.
     *
     * @return 200 with the list of books
     */
    @GET
    @Operation(summary = "Récupérer tous les livres")
    @APIResponse(
            responseCode = "200",
            description = "Liste des livres",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = BookResponse.class)
            )
    )
    public Response getAllBooks() {
        List<BookResponse> bookResponses = bookService.findAll();
        return Response.ok(bookResponses).build();
    }

    /**
     * Retrieves a book by its identifier.
     *
     * @param id identifier of the book
     * @return 200 with the book, or 404 if not found
     */
    @GET
    @Operation(summary = "Récupérer un livre par son ID")
    @APIResponse(responseCode = "200", description = "Livre trouvé",
            content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @APIResponse(responseCode = "404", description = "Livre non trouvé")
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") Long id) {
        BookResponse book = bookService.findById(id);
        return Response.status(Response.Status.CREATED)
                .entity(book)
                .build();
    }

    /**
     * Creates a new book.
     *
     * @param request the data of the book to create
     * @return 201 with the created book, or 400 if the request is invalid
     */
    @POST
    @Operation(summary = "Créer un livre")
    @APIResponse(responseCode = "201", description = "Livre créé")
    @APIResponse(responseCode = "400", description = "Requête invalide")
    @Transactional
    public Response createBook(BookRequest request) {
         BookResponse book = bookService.create(request);
        return Response.status(Response.Status.CREATED)
                .entity(book)
                .build();
    }
}
