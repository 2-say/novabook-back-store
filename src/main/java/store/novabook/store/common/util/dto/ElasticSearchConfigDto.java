package store.novabook.store.common.util.dto;

public record ElasticSearchConfigDto(
	String uris,
	String id,
	String password
) {
}
