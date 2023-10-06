package com.epam.mentoring.taf;

import io.restassured.http.ContentType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class UserSignInTest extends AbstractTest {
    private final String username = "Tom Marvolo Riddle";
    private final String email = "tom_marvolo@example.com";
    private final String password = "Voldemort";

    @Test
    public void uiVerification() {
        WebElement emailFiled = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));

        driver.findElement(By.xpath("//li/a[text()=' Sign in ']")).click();
        emailFiled.clear();
        emailFiled.sendKeys(email);
        passwordField.clear();
        passwordField.sendKeys(password);
        driver.findElement(By.xpath("//button[contains(text(),'Sign in')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[contains(@class,'navbar-nav')]/li[4]/a")));
        String actualUserName = driver.findElement(By.xpath("//ul[contains(@class,'navbar-nav')]/li[4]/a")).getText();
        Assert.assertEquals(actualUserName, username);
    }

    @Test
    public void uiNegativeVerification() {
        driver.findElement(By.xpath("//li/a[text()=' Sign in ']")).click();
        driver.findElement(By.xpath("//input[@placeholder='Email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@placeholder='Password']")).sendKeys("wrong_password");
        driver.findElement(By.xpath("//button[contains(text(),'Sign in')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@class='error-messages']/li")));
        String actualUserName = driver.findElement(By.xpath("//ul[@class='error-messages']/li")).getText();
        Assert.assertEquals(actualUserName, "email or password is invalid");
    }

    @Test
    public void apiVerification() {
        given().baseUri(API_URL).when().contentType(ContentType.JSON).body(String.format("{\"user\":{\"email\":\"%s\",\"password\":\"%s\"}}", email, password)).post("/api/users/login").then().statusCode(200).body("user.email", is(email));
    }

    @Test
    public void apiNegativeVerification() {
        given().baseUri(API_URL).when().contentType(ContentType.JSON).body(String.format("{\"user\":{\"email\":\"%s\",\"password\":\"%s\"}}", email, "wrong_password")).post("/api/users/login").then().statusCode(403).body("errors.'email or password'", hasItem("is invalid"));
    }

}
