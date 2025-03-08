package org.example.magazyn.service;

import org.example.magazyn.dto.ZoneDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.Zone;

import java.util.List;

public interface ZoneService {
    Zone createZone(ZoneDto zoneDto);
    List<Zone> getAllZones();
    Zone getZoneById(Long id);
    Product assignProductToZone(Long productId, Long zoneId) throws Exception;
    void removeProductFromZone(Long productId, Long zoneId);
    List<Product> getProductsInZone(Long zoneId);
    Zone updateZone(Long zoneId, ZoneDto zoneDto);
    void deleteZone(Long id);
}