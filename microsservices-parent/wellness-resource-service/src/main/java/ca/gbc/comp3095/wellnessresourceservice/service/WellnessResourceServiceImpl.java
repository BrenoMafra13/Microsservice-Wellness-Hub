package ca.gbc.comp3095.wellnessresourceservice.service;

import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceResponse;
import ca.gbc.comp3095.wellnessresourceservice.exception.WellnessResourceNotFoundException;
import ca.gbc.comp3095.wellnessresourceservice.model.WellnessResource;
import ca.gbc.comp3095.wellnessresourceservice.repository.WellnessResourceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "resources")
public class WellnessResourceServiceImpl implements WellnessResourceService {

    private final WellnessResourceRepository wellnessResourceRepository;

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
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
    @Cacheable(key = "'all'")
    @Transactional(readOnly = true)
    public List<WellnessResourceResponse> getAllResources() {
        log.info("Fetching all wellness resources");
        return wellnessResourceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Cacheable(key = "'id:' + #id")
    @Transactional(readOnly = true)
    public WellnessResourceResponse getResourceById(Long id) {
        log.info("Fetching resource with id {}", id);
        WellnessResource resource = findResource(id);
        return mapToResponse(resource);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public WellnessResourceResponse updateResource(Long id, WellnessResourceRequest resourceRequest) {
        log.info("Updating resource with id {}", id);
        WellnessResource resource = findResource(id);

        resource.setTitle(resourceRequest.title());
        resource.setDescription(resourceRequest.description());
        resource.setCategory(resourceRequest.category());
        resource.setUrl(resourceRequest.url());

        WellnessResource updated = wellnessResourceRepository.save(resource);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteResource(Long id) {
        log.info("Deleting resource with id {}", id);
        WellnessResource resource = findResource(id);
        wellnessResourceRepository.delete(resource);
    }

    @Override
    @Cacheable(key = "'category:' + (#category == null ? 'all' : #category.toLowerCase())")
    @Transactional(readOnly = true)
    public List<WellnessResourceResponse> getResourcesByCategory(String category) {
        log.info("Fetching resources by category: {}", category);
        return wellnessResourceRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Cacheable(key = "'search:' + (#keyword == null ? 'all' : #keyword.toLowerCase())")
    @Transactional(readOnly = true)
    public List<WellnessResourceResponse> searchResources(String keyword) {
        log.info("Searching resources with keyword: {}", keyword);
        if (keyword == null || keyword.isBlank()) {
            return wellnessResourceRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return wellnessResourceRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private WellnessResource findResource(Long id) {
        return wellnessResourceRepository.findById(id)
                .orElseThrow(() -> new WellnessResourceNotFoundException(id));
    }

    private WellnessResourceResponse mapToResponse(WellnessResource resource) {
        return new WellnessResourceResponse(
                resource.getId(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getCategory(),
                resource.getUrl()
        );
    }
}
