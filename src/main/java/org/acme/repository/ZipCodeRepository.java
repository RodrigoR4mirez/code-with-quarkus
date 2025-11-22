package org.acme.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entity.ZipCodeEntity;

@ApplicationScoped
public class ZipCodeRepository implements PanacheRepositoryBase<ZipCodeEntity, String> {
}