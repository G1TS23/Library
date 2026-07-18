package org.library.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Réponse paginée contenant les resultats de recherche de OpenLibrary")
public class PagedResponse {
    @Schema(description = "Nombre total de résultats", examples = {"152"})
    public Integer total;
    @Schema(description = "Décalage dans les résultats", examples = {"25"})
    public Integer offset;
    @Schema(description = "Nombre de résultats par page", examples = {"50"})
    public Integer limit;
    @Schema(description = "Liste de BookResponse", examples = {"[" +
            "{\"title\":\"Clean Code\"," +
            "\"author\":\"Robert Martin\"," +
            "\"year\":2008" +
            "}," +
            "{\"title\":\"The Pragmatic Programmer\"," +
            "\"author\":\"David Thomas\"," +
            "\"year\":1999" +
            "}" +
            "]"})
    public List<BookResponse> items;

    public PagedResponse() {}
    public PagedResponse(Integer total, Integer offset, Integer limit, List<BookResponse> items) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
        this.items = items;
    }
}
