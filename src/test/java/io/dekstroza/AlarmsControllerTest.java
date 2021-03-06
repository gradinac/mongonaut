package io.dekstroza;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MicronautTest
public class AlarmsControllerTest {

    @Inject
    @Client("/mongonaut")
    private AlarmController mongonautClient;

    private static final Alarm TEST_ALARM = new Alarm(2, "Second Test Alarm", "CRITICAL");

    @Order(1)
    @Test
    public void testPostAlarm() {
        HttpResponse<Alarm> response = mongonautClient.save(Single.just(TEST_ALARM)).blockingGet();
        assertEquals(201, response.code());
        assertEquals(TEST_ALARM, response.body());

    }

    @Order(2)
    @Test
    public void testGetAlarmByIdReturns__200__When_Present() {
        Flowable<Alarm> result = mongonautClient.findById(2);
        assertFalse(result.isEmpty().blockingGet());
        assertEquals(TEST_ALARM, result.singleElement().blockingGet());
    }

    @Order(3)
    @Test
    public void testGetAlarmById__Throws404__WhenNotFound() {
        Flowable<Alarm> result = mongonautClient.findById(3);
        assertTrue(result.isEmpty().blockingGet());

    }

    @Order(4)
    @Test
    public void testGetAlarmsBySeverity__Returns__200__When__Present() {
        List<Alarm> alarms = mongonautClient.findBySeverity("CRITICAL").toList().blockingGet();
        assertEquals(1, alarms.size());

    }

    @Order(5)
    @Test
    public void testGetAlarmsBySeverity__Returns__200__AND__EmptyArray__When__Present() {
        List<Alarm> alarms = mongonautClient.findBySeverity("MINOR").toList().blockingGet();
        assertTrue(alarms.isEmpty());

    }
}
