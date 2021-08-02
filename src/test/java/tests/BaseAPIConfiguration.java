package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.FilesHelper;

import static io.restassured.RestAssured.given;

public class BaseAPIConfiguration {
    private FilesHelper fileHelper = new FilesHelper();

    {
        RestAssured.baseURI = "https://userapi.webinar.ru/v3/";
    }

    protected RequestSpecification postRequestSpec() {
        return baseConfig().contentType(ContentType.MULTIPART);
    }

    private RequestSpecification baseConfig() {
        return given().header("X-Auth-Token", fileHelper.getToken());
    }

    protected RequestSpecification commonSpec(){
        return baseConfig().contentType(ContentType.URLENC);
    }

}
