package fr.openobservatory.backend.models;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchResults<T> {
        private final List<T> data;
        private final int count;
        private final int totalCount;
        private final int page;
        private final int pageCount;
}
