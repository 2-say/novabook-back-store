package store.novabook.store.tag.controller.docs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import store.novabook.store.tag.dto.CreateTagRequest;
import store.novabook.store.tag.dto.CreateTagResponse;
import store.novabook.store.tag.dto.GetTagListResponse;
import store.novabook.store.tag.dto.GetTagResponse;
import store.novabook.store.tag.dto.UpdateTagRequest;

@Tag(name = "Tag API")
public interface TagControllerDocs {
	ResponseEntity<Page<GetTagResponse>> getTagAll(Pageable pageable);

	ResponseEntity<GetTagListResponse> getTagAllList();

	ResponseEntity<GetTagResponse> getTag(@PathVariable Long id);

	ResponseEntity<CreateTagResponse> createTag(@RequestBody @Valid CreateTagRequest createTagRequest);

	ResponseEntity<Void> updateTag(@Valid @RequestBody UpdateTagRequest updateTagRequest, @PathVariable Long id);

	ResponseEntity<Void> deleteTag(@PathVariable Long id);
}
