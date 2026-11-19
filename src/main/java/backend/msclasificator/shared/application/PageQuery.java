package backend.msclasificator.shared.application;

public record PageQuery(int page, int size, String sortBy, String direction) {
}
