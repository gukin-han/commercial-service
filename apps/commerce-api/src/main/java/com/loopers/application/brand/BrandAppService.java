package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandId;
import com.loopers.domain.brand.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BrandAppService {

    private final BrandRepository brandRepository;

    public Brand findByBrandId(BrandId brandId) {
        return brandRepository.findById(brandId.value()).orElseThrow(EntityNotFoundException::new);
    }
}
