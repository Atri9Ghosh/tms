package com.atri.tms.service;

import com.atri.tms.dto.LoadRequest;
import com.atri.tms.dto.LoadResponse;
import com.atri.tms.entity.Load;
import com.atri.tms.entity.LoadStatus;
import com.atri.tms.entity.WeightUnit;
import com.atri.tms.exception.ResourceNotFoundException;
import com.atri.tms.repository.BidRepository;
import com.atri.tms.repository.BookingRepository;
import com.atri.tms.repository.LoadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private LoadService loadService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------------
    // HELPER: Create Sample Request
    // ----------------------------------------------------------------
    private LoadRequest createSampleRequest() {
        LoadRequest req = new LoadRequest();
        req.setShipperId("SHP1");
        req.setLoadingCity("CityA");
        req.setUnloadingCity("CityB");
        req.setLoadingDate(Instant.parse("2025-01-10T10:00:00Z"));
        req.setProductType("Steel");
        req.setWeight(12.5);
        req.setWeightUnit(WeightUnit.TON);
        req.setTruckType("FLATBED");
        req.setNoOfTrucks(3);
        return req;
    }

    // ----------------------------------------------------------------
    // HELPER: Create Sample Load Entity
    // ----------------------------------------------------------------
    private Load createSampleLoad(UUID id) {
        Load load = new Load();
        load.setLoadId(id);
        load.setShipperId("SHP1");
        load.setLoadingCity("CityA");
        load.setUnloadingCity("CityB");
        load.setLoadingDate(Instant.parse("2025-01-10T10:00:00Z"));
        load.setProductType("Steel");
        load.setWeight(12.5);
        load.setWeightUnit(WeightUnit.TON);
        load.setTruckType("FLATBED");
        load.setNoOfTrucks(3);
        load.setStatus(LoadStatus.POSTED);
        load.setDatePosted(Instant.now());
        return load;
    }

    // ----------------------------------------------------------------
    // TEST: createLoad()
    // ----------------------------------------------------------------
    @Test
    void testCreateLoad() {

        LoadRequest req = createSampleRequest();

        Load saved = createSampleLoad(UUID.randomUUID());

        when(loadRepository.save(any(Load.class))).thenReturn(saved);

        LoadResponse response = loadService.createLoad(req);

        assertNotNull(response);
        assertEquals(saved.getLoadId(), response.getLoadId());
        assertEquals("SHP1", response.getShipperId());

        verify(loadRepository, times(1)).save(any(Load.class));
    }

    // ----------------------------------------------------------------
    // TEST: getLoad()
    // ----------------------------------------------------------------
    @Test
    void testGetLoad() {
        UUID id = UUID.randomUUID();
        Load sample = createSampleLoad(id);

        when(loadRepository.findById(id)).thenReturn(Optional.of(sample));

        LoadResponse res = loadService.getLoad(id);

        assertNotNull(res);
        assertEquals(id, res.getLoadId());
        assertEquals("SHP1", res.getShipperId());

        verify(loadRepository, times(1)).findById(id);
    }

    // ----------------------------------------------------------------
    // TEST: getLoad() → Not found
    // ----------------------------------------------------------------
    @Test
    void testGetLoad_NotFound() {
        UUID id = UUID.randomUUID();

        when(loadRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loadService.getLoad(id));

        verify(loadRepository, times(1)).findById(id);
    }
}
