package ca.gbc.comp3095.wellnessresourceservice.service;

import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceResponse;
import ca.gbc.comp3095.wellnessresourceservice.model.WellnessResource;
import ca.gbc.comp3095.wellnessresourceservice.repository.WellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WellnessResourceServiceImpl implements WellnessResourceService {

    private final WellnessResourceRepository wellnessResourceRepository;

    @Override
    @CacheEvict(value = "resources", allEntries = true)
    public WellnessResourceResponse createResource(WellnessResourceRequest resourceRequest) {
        log.info("Creating new wellness resource: {}", resourceRequest.title());

        WellnessResource resource = WellnessResource.builder()
                .title(resourceRequest.title())
                .description(resourceRequest.description())
                .category(resourceRequest.category())
                .url(resourceRequest.url())
                .build();

        WellnessResource saved = wellnessResourceRepository.save(resource);

        return mapToResponse(saved);
    }

    @Override
    @Cacheable(value = "resources")
    public List<WellnessResourceResponse> getAllResources() {
        log.info("Fetching all wellness resources");
        return wellnessResourceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @CachePut(value = "resources", key = "#id")
    public WellnessResourceResponse updateResource(Long id, WellnessResourceRequest resourceRequest) {
        log.info("Updating resource with id {}", id);

        WellnessResource resource = wellnessResourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setTitle(resourceRequest.title());
        resource.setDescription(resourceRequest.description());
        resource.setCategory(resourceRequest.category());
        resource.setUrl(resourceRequest.url());

        WellnessResource updated = wellnessResourceRepository.save(resource);
        return mapToResponse(updated);
    }

    @Override
    @CacheEvict(value = "resources", key = "#id")
    public void deleteResource(Long id) {
        log.info("Deleting resource with id {}", id);
        wellnessResourceRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "resources", key = "#category")
    public List<WellnessResourceResponse> getResourcesByCategory(String category) {
        log.info("Fetching resources by category: {}", category);
        return wellnessResourceRepository.findAll().stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "resources", key = "#keyword")
    public List<WellnessResourceResponse> searchResources(String keyword) {
        log.info("Searching resources with keyword: {}", keyword);
        return wellnessResourceRepository.findAll().stream()
                .filter(r -> r.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || r.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::mapToResponse)
                .toList();
    }

    private WellnessResourceResponse mapToResponse(WellnessResource resource) {
        return new WellnessResourceResponse(
                resource.getId(), resource.getTitle(), resource.getDescription(),
                resource.getCategory(), resource.getUrl()
        );
    }
}
