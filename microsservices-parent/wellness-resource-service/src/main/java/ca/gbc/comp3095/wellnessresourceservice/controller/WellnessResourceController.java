package ca.gbc.comp3095.wellnessresourceservice.controller;

import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceResponse;
import ca.gbc.comp3095.wellnessresourceservice.service.WellnessResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class WellnessResourceController {

    private final WellnessResourceService _wellnessResourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WellnessResourceResponse createResource(@RequestBody WellnessResourceRequest resourceRequest) {
        return _wellnessResourceService.createResource(resourceRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WellnessResourceResponse> getAllResources(
            @RequestParam(required = false) String category) {

        if (category != null && !category.isBlank()) {
            return _wellnessResourceService.getResourcesByCategory(category);
        }

        return _wellnessResourceService.getAllResources();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WellnessResourceResponse updateResource(
            @PathVariable Long id,
            @RequestBody WellnessResourceRequest resourceRequest) {
        return _wellnessResourceService.updateResource(id, resourceRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long id) {
        _wellnessResourceService.deleteResource(id);
    }
}
