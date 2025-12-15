package com.lorecodex.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PagedResponse<T> from(Page<?> page, List<T> content) {
        return PagedResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public static <T> PagedResponse<T> from(int pageNumber, int pageSize, List<T> content, boolean hasNext) {
        return PagedResponse.<T>builder()
                .content(content)
                .page(pageNumber)
                .size(pageSize)
                .totalElements((long) pageNumber * pageSize + content.size())
                .totalPages(pageNumber + (hasNext ? 2 : 1))
                .hasNext(hasNext)
                .hasPrevious(pageNumber > 0)
                .build();
    }
}
