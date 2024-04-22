package com.yule.crawling_sketchbook.memos.total;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TotalMemo {
    @Test
    @DisplayName("multi thread")
    void thread() throws InterruptedException, ExecutionException {
        ExecutorService thread = Executors.newFixedThreadPool(5);
        int cnt = 0;
        while (true) {
            System.out.println(++cnt);
            Thread.sleep(1000);
            if (cnt == 5) break;
        }
        Future<?> naver = thread.submit(this::naverTest2);
        Future<?> google = thread.submit(this::googleTest2);
        naver.get();
        google.get();


    }

    @Test
    @DisplayName("google2")
    void googleTest2() {

        String query = "용산동";
        int userGoogleRank = 1;
        int rankSize = userGoogleRank == 1 ? userGoogleRank * 3 : 0;
        int page = 1;
        ChromeDriver driver = new ChromeDriver();


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
        List<BaseSiteInfo> result = new ArrayList<>();
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

                    GoogleResultObj resultObj = new GoogleResultObj();
                    result.add(resultObj);
                    resultObj.getTitle().add(atag.findElement(By.xpath("./h3")).getText());
                    resultObj.setLink(atag.getAttribute("href"));
                    List<WebElement> children = thisDiv.findElements(By.xpath("./div/div/div"));
                    int totalSize = children.size();
                    int contentIdx = totalSize - 2;
                    List<WebElement> spans = children.get(contentIdx).findElements(By.xpath("./div/span"));
                    for (WebElement span : spans) {
                        resultObj.getContent().add(span.getText());
                    }
                    WebElement iconEl = atag.findElement(By.xpath("./div/div/span/div/img"));
                    resultObj.setIconPath(iconEl.getAttribute("src"));

                } catch (NoSuchElementException ignore) {
                }
            }
        } finally {
            driver.quit();
        }

        result.forEach(System.out::println);
    }

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

            try {
                WebElement iconEl = comm.findElement(By.xpath("./../div[contains(@class, user_box)]//img[@height=24]"));
                naverResultObj.setIconPath(iconEl.getAttribute("src"));
            } catch (NoSuchElementException ignore) {

            }

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

            try {
                WebElement iconEl = n.findElement(By.xpath(".//span[contains(@class, \"thumb_box\")]/img"));
                naverResultObj.setIconPath(iconEl.getAttribute("src"));
            } catch (NoSuchElementException ignore) {
            }
        }
        System.out.println("------- news -------");
        result.forEach(System.out::println);
        System.out.println("---------------");

        result.clear();

        // 지식인
        List<WebElement> kins = driver.findElements(By.xpath("//ul[@class=\"lst_nkin\"]/li"));
        for (WebElement kin : kins) {
            WebElement titleEl = kin.findElement(By.xpath(".//a[contains(@class, \"question_text\")]"));
            WebElement contentEl = kin.findElement(By.xpath(".//a[contains(@class, \"answer_text\")]"));
            NaverResultObj naverResultObj = new NaverResultObj();
            result.add(naverResultObj);

            naverResultObj.getTitle().add(titleEl.getText());
            naverResultObj.setLink(titleEl.getAttribute("href"));
            naverResultObj.getContent().add(contentEl.getText());

        }
        System.out.println("------- news -------");
        result.forEach(System.out::println);
        System.out.println("---------------");


        driver.quit();
    }

}

// 전체개수의 카테고리별 선호도 비율 -> 각 카테고리별 선호 사이트별 비율 의 단계로 상위 노출 정렬.
enum SiteCategories {
    COMMUNITY, INFO, NEWS, KIN
}

abstract class BaseSiteInfo {
    List<String> title = new ArrayList<>();
    String link;
    List<String> content = new ArrayList<>();
    SiteCategories category;
    String iconPath;

    @Override
    public String toString() {
        return "BaseSiteInfo{" +
               "title=" + title +
               ", link='" + link + '\'' +
               ", content=" + content +
               ", category=" + category +
               ", iconPath='" + iconPath + '\'' +
               '}';
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
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

    public SiteCategories getCategory() {
        return category;
    }

    public void setCategory(SiteCategories category) {
        this.category = category;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
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
               ", category=" + category +
               ", iconPath='" + iconPath + '\'' +
               '}';
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

}

