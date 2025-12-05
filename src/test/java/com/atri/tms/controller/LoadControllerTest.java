package com.atri.tms.controller;

import com.atri.tms.dto.LoadRequest;
import com.atri.tms.dto.LoadResponse;
import com.atri.tms.entity.LoadStatus;
import com.atri.tms.service.LoadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(LoadController.class)
class LoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoadService loadService;

    @Test
    void testGetLoadById() throws Exception {

        UUID id = UUID.randomUUID();
        LoadResponse resp = LoadResponse.builder()
                .loadId(id)
                .shipperId("SHP1")
                .loadingCity("A")
                .unloadingCity("B")
                .loadingDate(Instant.now())
                .productType("Steel")
                .weight(10)
                .weightUnit(null)
                .truckType("FLATBED")
                .noOfTrucks(2)
                .status(LoadStatus.POSTED)
                .datePosted(Instant.now())
                .build();

        when(loadService.getLoad(id)).thenReturn(resp);

        mockMvc.perform(get("/load/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loadId").value(id.toString()));

        verify(loadService, times(1)).getLoad(id);
    }
}
