package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.TestTakerRequest;
import com.benorim.testsystem.controller.api.response.TestTakerResponse;
import com.benorim.testsystem.entity.TestTaker;
import com.benorim.testsystem.service.TestTakerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.benorim.testsystem.mapper.TestTakerMapper.mapTestTakerToTestTakerResponse;

@RestController
@RequestMapping("/api/v1/testTaker")
public class TestTakerApi {

    private final TestTakerService testTakerService;

    public TestTakerApi(TestTakerService testTakerService) {
        this.testTakerService = testTakerService;
    }

    @PostMapping("")
    public ResponseEntity<TestTakerResponse> createTestTaker(@Valid @RequestBody TestTakerRequest testTakerRequest) {
        TestTaker testTaker = testTakerService.createTestTaker(testTakerRequest.username());

        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(testTaker.getId())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uri);
        return new ResponseEntity<>(mapTestTakerToTestTakerResponse(testTaker), headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestTakerResponse> getTestTaker(@PathVariable Long id) {
        TestTakerResponse response = mapTestTakerToTestTakerResponse(testTakerService.getTestTaker(id));
        return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
