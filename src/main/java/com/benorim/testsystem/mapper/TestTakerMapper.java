package com.benorim.testsystem.mapper;

import com.benorim.testsystem.controller.api.response.TestTakerResponse;
import com.benorim.testsystem.entity.TestTaker;

public class TestTakerMapper {
    public static TestTakerResponse mapTestTakerToTestTakerResponse(TestTaker testTaker) {
        if (testTaker == null) return  null;

        return  new TestTakerResponse(testTaker.getId(), testTaker.getUsername());
    }
}
