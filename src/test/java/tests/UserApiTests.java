package tests;

import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.containsString;
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
        String toDate = "2021-08-01";
        String eventName = "test123";

        commonSpec()
                .pathParam("fromDate", fromDate)
                .pathParam("toDate", toDate)
                .pathParam("status[0]", "Active")
                .pathParam("name", eventName)
                .when()
                .get("/organization/events/schedule?{fromDate}/{toDate}/{status[0]}/{name}")
                .then()
                .statusCode(200)
                .assertThat()
                .body("id[0]", equalTo("8915623"))
                .body("name[0]", equalTo(eventName))
                .body("type[0]", equalTo("webinar"))
                .body("createUserId[0]", equalTo("33389089"));

    }

    @Test
    void getEventInfoById() {
        String eventId = "8915623";
        commonSpec()
                .when()
                .get("/organization/events/{eventId}", eventId)
                .then()
                .statusCode(200)
                .assertThat()
                .body("id", equalTo(Integer.parseInt(eventId)))
                .body("name", equalTo("test123"))
                .body("type", equalTo("webinar"))
                .body("createUserId", equalTo(33389089));
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
        String fromDate = "2021-07-01";
        String toDate = "2021-08-01";

        commonSpec()
                .pathParams("from", fromDate, "to", toDate)
                .when()
                .get("/stats/users/visits?{from}/{to}")
                .then()
                .statusCode(400)
                .assertThat()
                .body("error.message", containsString("Email or contact ID is mandatory"));

    }


}
