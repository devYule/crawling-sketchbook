package com.yule.crawling_sketchbook.memos.google;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class GoogleMemo {


    @Test
    @DisplayName("google2")
    void googleTest2() {

        String query = "용산동";
        int userGoogleRank = 1;
        int rankSize = userGoogleRank == 1 ? userGoogleRank * 3 : 0;
        int page = 1;
        ChromeDriver driver = new ChromeDriver();
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));


        driver.get("https://www.google.com");

        WebElement element = driver.findElement(By.cssSelector("[name='q'"));
        element.sendKeys(query);
        element.submit();
        List<WebElement> divs;
        long tryDurationStart = System.currentTimeMillis();
        while (driver.findElements(By.xpath("//*[@id='rso']/div")).size() < page * rankSize) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (System.currentTimeMillis() - tryDurationStart > 10000) throw new RuntimeException();
        }
        divs = driver.findElements(By.xpath("//*[@id='rso']/div"));

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5));
        List<ResultObj> result = new ArrayList<>();
        int size = divs.size();
        int lastIdx = size - 1;
        try {
            for (int i = 0; i < size; i++) {
                WebElement thisDiv = divs.get(i);
                try {
                    if (i == lastIdx) {
                        thisDiv = thisDiv.findElement(By.xpath("./div"));
                    }
                    //*[@id="rso"]/div[2]  /div/div/div[1]/div/div/span/a
                    WebElement atag = thisDiv.findElement(By.xpath("./div/div/div[1]/div/div/span/a"));

                    ResultObj resultObj = new ResultObj();
                    result.add(resultObj);
                    resultObj.setTitle(atag.findElement(By.xpath("./h3")).getText());
                    resultObj.setLink(atag.getAttribute("href"));
                    List<WebElement> children = thisDiv.findElements(By.xpath("./div/div/div"));
                    int totalSize = children.size();
                    int contentIdx = totalSize - 2;
                    List<WebElement> spans = children.get(contentIdx).findElements(By.xpath("./div/span"));
                    for (WebElement span : spans) {
                        resultObj.getContent().add(span.getText());
                    }

                } catch (NoSuchElementException ignore) {
                }
            }
        } finally {
            driver.quit();
        }

        result.forEach(System.out::println);
    }

}


class ResultObj {
    String title;
    String link;
    List<String> content = new ArrayList<>();

    @Override
    public String toString() {
        return "ResultObj{" +
               "title='" + title + '\'' +
               ", link='" + link + '\'' +
               ", content=" + content +
               '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
