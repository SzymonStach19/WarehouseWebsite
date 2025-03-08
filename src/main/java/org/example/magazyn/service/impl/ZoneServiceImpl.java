package org.example.magazyn.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.dto.ZoneDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.Zone;
import org.example.magazyn.repository.ProductRepository;
import org.example.magazyn.repository.ZoneRepository;
import org.example.magazyn.service.ZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Zone createZone(ZoneDto zoneDto) {
        // Sprawdzenie czy strefa o takiej nazwie już istnieje
        if (zoneRepository.findByName(zoneDto.getName()) != null) {
            throw new RuntimeException("Strefa o tej nazwie już istnieje");
        }

        Zone zone = new Zone();
        zone.setName(zoneDto.getName());
        zone.setMaxCapacity(zoneDto.getMaxCapacity());
        zone.setCurrentWeight(0.0);
        return zoneRepository.save(zone);
    }

    @Override
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    @Override
    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strefa nie znaleziona"));
    }

    @Override
    @Transactional
    public Product assignProductToZone(Long productId, Long zoneId) throws Exception {
        Zone zone = getZoneById(zoneId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produkt nie znaleziony"));

        // Sprawdzenie czy produkt jest już przypisany do innej strefy
        if (product.getZone() != null) {
            throw new Exception("Produkt jest już przypisany do innej strefy");
        }

        // Sprawdzenie czy produkt zmieści się w strefie
        double totalProductWeight = product.getWeight() * product.getQuantity();
        double newTotalWeight = zone.getCurrentWeight() + totalProductWeight;

        if (newTotalWeight > zone.getMaxCapacity()) {
            throw new Exception("Niewystarczająca przestrzeń w strefie. Przekroczono maksymalną wagę.");
        }

        // Przypisanie produktu do strefy i aktualizacja wagi
        product.setZone(zone);
        zone.setCurrentWeight(newTotalWeight);

        zoneRepository.save(zone);
        return productRepository.save(product);
    }

    @Override
    public void removeProductFromZone(Long productId, Long zoneId) {
        Zone zone = getZoneById(zoneId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produkt nie znaleziony"));

        if (!product.getZone().getId().equals(zoneId)) {
            throw new RuntimeException("Produkt nie należy do tej strefy");
        }

        // Update zone's current weight
        double productTotalWeight = product.getWeight() * product.getQuantity();
        zone.setCurrentWeight(zone.getCurrentWeight() - productTotalWeight);

        // Remove product from zone
        product.setZone(null);

        // Save changes
        productRepository.save(product);
        zoneRepository.save(zone);
    }

    @Override
    public List<Product> getProductsInZone(Long zoneId) {
        Zone zone = getZoneById(zoneId);
        return zone.getProducts();
    }

    @Override
    @Transactional
    public Zone updateZone(Long zoneId, ZoneDto zoneDto) {
        Zone existingZone = getZoneById(zoneId);

        // Sprawdzenie nazwy strefy
        if (!existingZone.getName().equals(zoneDto.getName())) {
            if (zoneRepository.findByName(zoneDto.getName()) != null) {
                throw new RuntimeException("Strefa o tej nazwie już istnieje");
            }
            existingZone.setName(zoneDto.getName());
        }

        // Sprawdzenie czy nowa maksymalna pojemność jest większa od aktualnej wagi
        if (zoneDto.getMaxCapacity() < existingZone.getCurrentWeight()) {
            throw new RuntimeException("Nowa maksymalna pojemność jest mniejsza niż aktualna waga produktów w strefie");
        }

        existingZone.setMaxCapacity(zoneDto.getMaxCapacity());
        return zoneRepository.save(existingZone);
    }

    @Override
    @Transactional
    public void deleteZone(Long id) {
        Zone zone = getZoneById(id);

        // Sprawdzenie, czy strefa jest pusta
        if (!zone.getProducts().isEmpty()) {
            throw new RuntimeException("Nie można usunąć strefy, która zawiera produkty");
        }

        zoneRepository.delete(zone);
    }
}