package tests;

import io.restassured.response.Response;
import models.Event;
import models.StatisticsData;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserApiTests extends BaseAPIConfiguration {


    @Test
    void createEventTest() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Integer userId = 33389089;

        Response response = postRequestSpec()
                .multiPart("name", "test123")
                .multiPart("access", "10")
                .multiPart("startsAt[date][year]", calendar.get(Calendar.YEAR))
                .multiPart("startsAt[date][month]", calendar.get(Calendar.MONTH))
                .multiPart("startsAt[date][day]", calendar.get(Calendar.DAY_OF_MONTH))
                .multiPart("startsAt[time][hour]", calendar.get(Calendar.HOUR_OF_DAY))
                .multiPart("startsAt[time][minute]", calendar.get(Calendar.MINUTE))
                .multiPart("ownerId", userId)
                .multiPart("lectorIds[0]", userId)
                .post("events")
                .then()
                .statusCode(201)
                .extract().response();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.getBody().asString()).contains("eventId");
        softly.assertThat(response.getBody().asString().contains("link"));
        softly.assertAll();
    }

    @Test
    void getEventsScheduleInfo() {
        String fromDate = "2021-07-01";
        String toDate = "2021-08-06";
        String eventName = "test123";

        Event[] events = commonSpec()
                .pathParams("from", fromDate, "to", toDate, "status[0]", "Active", "name", eventName)
                .when()
                .get("/organization/events/schedule?from={from}&{to}/{status[0]}/{name}")
                .then()
                .statusCode(200)
                .log()
                .body()
                .extract()
                .as(Event[].class);

        assertThat(events).as("Events %d number doesnt equal to the expected one %d").hasSize(10);


    }

    @Test
    void getEventInfoById() {

        String eventId = "8915623";
        String expectedEventName = "test123";
        Integer expectedCreatorId = 33389089;
        String expectedType = "webinar";

        Event event = commonSpec()
                .when()
                .get("/organization/events/{eventId}", eventId)
                .then()
                .statusCode(200)
                .log()
                .body()
                .extract()
                .as(Event.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(event.getId()).as("Obtained event id (%d) doesn't equal to the expected one (%d)",
                event.getId(), eventId).isEqualTo(Integer.parseInt(eventId));

        softly.assertThat(event.getName()).as("Obtained event name (%s) doesn't equal to the expected one (%s)",
                event.getName(), expectedEventName).isEqualTo(expectedEventName);

        softly.assertThat(event.getType()).as("Obtained event type (%s) doesn't equal to the expected one (%s)",
                event.getType(), expectedType).isEqualTo(expectedType);

        softly.assertThat(event.getCreateUser()).as("Create user is null").isNotNull();

        softly.assertThat(event.getCreateUserId()).as("Obtained creator id (%d) doesn't equal to the expected one (%d)",
                event.getCreateUserId(), expectedCreatorId).isEqualTo(expectedCreatorId);
        softly.assertAll();

    }

    @Test
    void addParticipantModerationTest() {
        String eventId = "8915623";
        String[] participantIds = {"23345", "23346"};

        commonSpec()
                .formParam("participationIds[][]", participantIds)
                .formParam("isAccepted", true)
                .when()
                .put("/events/" + eventId + "/moderate")
                .then()
                .statusCode(200)
                .assertThat()
                .body("count", equalTo(((int) Arrays.stream(participantIds).count())));

    }

    @Test
    void getEventVisitStatistics() {
        String fromDate = "2021-08-05";
        String toDate = "2021-08-06";
        String eventId = "8927615";
        String expectedUserId = "33389089";

        StatisticsData[] statisticsData = commonSpec()
                .pathParams("from", fromDate, "to", toDate, "eventId", eventId)
                .when()
                .get("/stats/users?{from}/{to}/{eventId}")
                .then()
                .statusCode(200)
                .log()
                .body()
                .extract()
                .as(StatisticsData[].class);

        boolean checkUserExists = Arrays.stream(statisticsData).anyMatch(c -> c.getId().equals(expectedUserId));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(statisticsData).as("The actual statistics data size %d doesn't equal to the expected one %d", Arrays.asList(statisticsData).size(), 2).hasSize(2);
        softly.assertThat(checkUserExists).as("User with id %s not found", expectedUserId).isTrue();
        softly.assertAll();

    }

}
