package org.library.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.dto.PagedResponse;
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
                    schema = @Schema(
                            type = SchemaType.ARRAY,
                            implementation = BookResponse.class
                    )
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
        return Response.ok(book)
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
    @APIResponse(
            responseCode = "201",
            description = "Livre créé")
    @APIResponse(responseCode = "400", description = "Requête invalide")
    @Transactional
    @RolesAllowed("ADMIN")
    public Response createBook(@Valid BookRequest request) {
         BookResponse book = bookService.create(request);
        return Response.status(Response.Status.CREATED)
                .entity(book)
                .build();
    }

    /**
     * Deletes a book by its identifier.
     *
     * @param id of the book to delete
     * @return 204 if the book was deleted, or 404 if not found
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un livre")
    @APIResponse(responseCode = "204", description = "Livre supprimé")
    @APIResponse(responseCode = "404", description = "Livre non trouvé")
    @RolesAllowed("ADMIN")
    public Response deleteBook(@PathParam("id") Long id) {
        if(bookService.deleteById(id)) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Searches for books by title in OpenLibrary.
     *
     * @param title the title to search for
     * @param offset the starting point for pagination
     * @param limit the maximum number of results to return per page
     * @return a paged response containing the search results
     */
    @GET
    @Operation(summary = "Récupérer une liste de livres de OpenLibrary")
    @APIResponse(
            responseCode = "200",
            description = "Livre(s) récupéré(s)",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = PagedResponse.class
                    )
            ))
    @Path("/search")
    public Response searchByTitleFromOpenLibrary(
            @QueryParam("title")
            String title,
            @QueryParam("offset")
            @DefaultValue("0")
            @Min(value = 0, message = "Le décalage doit être positif ou nul")
            Integer offset,
            @QueryParam("limit")
            @DefaultValue("20")
            @Min(value = 1, message = "La limite doit être un nombre positif")
            @Max(value = 50, message = "La limite ne peut pas dépasser 50")
            Integer limit) {
        PagedResponse pagedResponse = bookService.searchByTitle(title, offset, limit);
        return Response.ok(pagedResponse)
                .build();
    }
}
