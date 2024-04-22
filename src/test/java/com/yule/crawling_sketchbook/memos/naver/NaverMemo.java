package com.yule.crawling_sketchbook.memos.naver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class NaverMemo {

    @Test
    @DisplayName("네이버 2")
    void naverTest2() {
        String query = "용산동";
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://www.naver.com");
        driver.findElement(By.xpath("//*[@id=\"query\"]")).sendKeys(query);
        driver.findElement(By.xpath("//button[@type=\"submit\"]")).click();

        List<BaseSiteInfo> result = new ArrayList<>();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 지식, 인플루언서, 맛집, 카페 등
        List<WebElement> community = driver.findElements(By.xpath("//div[@class=\"detail_box\"]"));
        for (WebElement comm : community) {
            NaverResultObj naverResultObj = new NaverResultObj();
            result.add(naverResultObj);
            WebElement titleEl = comm.findElement(By.xpath(
                    ".//a[contains(@class, \"title_link\") or contains(@class, \"title\")]"));
            WebElement contentEl = comm.findElement(
                    By.xpath(".//a[contains(@class, \"dsc_link\") or contains(@class, \"desc\")]"));
            naverResultObj.getTitle().add(titleEl.getText());
            naverResultObj.setLink(titleEl.getAttribute("href"));
            naverResultObj.getContent().add(contentEl.getText());
            naverResultObj.setCategory(SiteCategories.COMMUNITY);
        }
        System.out.println("------- community -------");
        result.forEach(System.out::println);
        System.out.println("---------------");

        result.clear();
        // 네이버 지식백과
        List<WebElement> naverWiki = driver.findElements(By.xpath("//div[@class=\"nkindic_basic\"]"));
        for (WebElement wiki : naverWiki) {
            NaverResultObj naverResultObj = new NaverResultObj();
            result.add(naverResultObj);
            WebElement titleEl = wiki.findElement(By.xpath(".//h3[contains(@class, \"tit_area\")]/a"));
            WebElement subTitleEl = null;
            try {
                subTitleEl = wiki.findElement(By.xpath(".//div[contains(@class, \"lnk_sub_tit\")]/a"));
            } catch (NoSuchElementException ignore) {
            }
            WebElement contentEl = wiki.findElement(By.xpath(".//div[contains(@class, \"content_desc\")]/a"));
            naverResultObj.getTitle().add(titleEl.getText());
            naverResultObj.setLink(titleEl.getAttribute("href"));
            if (subTitleEl != null) {
                naverResultObj.setSubTitle(subTitleEl.getText());
            }
            naverResultObj.getContent().add(contentEl.getText());
            naverResultObj.setCategory(SiteCategories.INFO);
        }
        System.out.println("------- wiki -------");
        result.forEach(System.out::println);
        System.out.println("---------------");

        result.clear();
        // 네이버 뉴스
        List<WebElement> news = driver.findElements(By.xpath("//ul[@class=\"list_news\"]/li"));
        for (WebElement n : news) {
            NaverResultObj naverResultObj = new NaverResultObj();
            result.add(naverResultObj);
            WebElement titleEl = n.findElement(By.xpath(".//a[contains(@class, \"news_tit\")]"));
            WebElement contentEl = n.findElement(By.xpath(".//div[contains(@class, \"dsc_wrap\")]/a"));
            naverResultObj.getTitle().add(titleEl.getText());
            naverResultObj.setLink(titleEl.getAttribute("href"));
            naverResultObj.getContent().add(contentEl.getText());
            naverResultObj.setCategory(SiteCategories.NEWS);
        }
        System.out.println("------- news -------");
        result.forEach(System.out::println);
        System.out.println("---------------");


        driver.quit();
    }

}

// 전체개수의 카테고리별 선호도 비율 -> 각 카테고리별 선호 사이트별 비율 의 단계로 상위 노출 정렬.
enum SiteCategories {
    COMMUNITY, INFO, NEWS
}

abstract class BaseSiteInfo {
    List<String> title = new ArrayList<>();
    String link;
    List<String> content = new ArrayList<>();
    SiteCategories category;

    @Override
    public String toString() {
        return "BaseSiteInfo{" +
               "title='" + title + '\'' +
               ", link='" + link + '\'' +
               ", content=" + content +
               ", category='" + category + '\'' +
               '}';
    }

    public List<String> getTitle() {
        return title;
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


    public SiteCategories getCategory() {
        return category;
    }

    public void setCategory(SiteCategories category) {
        this.category = category;
    }
}

class GoogleResultObj extends BaseSiteInfo {

}

class NaverResultObj extends BaseSiteInfo {
    String subTitle;

    @Override
    public String toString() {
        return "NaverResultObj{" +
               "subTitle='" + subTitle + '\'' +
               ", title=" + title +
               ", link='" + link + '\'' +
               ", content=" + content +
               ", category='" + category + '\'' +
               '}';
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

}
