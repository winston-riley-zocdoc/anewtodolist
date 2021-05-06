package com.jetbrains.teamcity.anewtodolist.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlowWebServiceTest {

    @Test
    public void slowTest() throws InterruptedException {
        Thread.sleep(25000);
        assertEquals(1,1);
    }
}
