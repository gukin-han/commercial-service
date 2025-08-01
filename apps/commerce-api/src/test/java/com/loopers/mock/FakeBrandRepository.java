package com.loopers.mock;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.mock.jpa.FakeJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeBrandRepository extends FakeJpaRepository<Brand> implements BrandRepository {

    @Override
    protected Long getId(Brand brand) {
        return brand.getId();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return data.stream()
                .filter(b -> b.getId() != null && b.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Brand> findAllByBrandIdIn(List<Long> brandIds) {
        return data.stream()
                .filter(b -> b.getId() != null && brandIds.contains(b.getId()))
                .collect(Collectors.toList());
    }
}
