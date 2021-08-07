package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private Integer id;
    private String name;
    private String status;
    private Integer access;
    private User createUser;
    private Integer createUserId;
    private String type;
}
