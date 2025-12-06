package ca.gbc.comp3095.wellnessresourceservice.service;

import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessresourceservice.dto.WellnessResourceResponse;

import java.util.List;

public interface WellnessResourceService {

    WellnessResourceResponse createResource(WellnessResourceRequest resourceRequest);
    List<WellnessResourceResponse> getAllResources();
    WellnessResourceResponse updateResource(Long id, WellnessResourceRequest resourceRequest);
    void deleteResource(Long id);
    List<WellnessResourceResponse> getResourcesByCategory(String category);
    List<WellnessResourceResponse> searchResources(String keyword);
}
