package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BrandService {

    private final BrandRepository brandRepository;

    public Brand findByBrandId(BrandId brandId) {
        return brandRepository.findById(brandId.getValue()).orElseThrow(() -> new CoreException(
                ErrorType.NOT_FOUND,
                String.format("브랜드 ID %s에 해당하는 브랜드를 찾을 수 없습니다.", brandId.getValue())
        ));
    }

    public List<Brand> findAllByIds(List<BrandId> brandIds) {
        return brandRepository.findAllByBrandIdIn(brandIds.stream().map(BrandId::getValue).collect(Collectors.toList()));
    }
}
