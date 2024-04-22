package com.yule.crawling_sketchbook;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class CrawlingTest {

    @Test
    void test() {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.get("https://www.google.com?q=test");
        WebElement element = chromeDriver.findElement(By.cssSelector("textarea"));
        element.submit();
        String title = chromeDriver.getTitle();
//        System.out.println("title = " + title);
        chromeDriver.findElement(By.id("rso"));
        String pageSource = chromeDriver.getPageSource();
//        System.out.println("pageSource = " + pageSource);
        List<WebElement> elements = chromeDriver.findElement(By.id("rso")).findElements(By.tagName("span"));
        for (WebElement webElement : elements) {
            String text = webElement.getText();
            System.out.println("text = " + text);
        }


//        chromeDriver.quit();
    }

    @Test
    void test2() {
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://google.com");
        driver.findElement(By.cssSelector("[name='q']")).sendKeys("test");
        String title = driver.switchTo().activeElement().getAttribute("title");
        System.out.println("title = " + title);
    }

    @Test
    @DisplayName("탭 열기, 탭 닫기, 컨트롤탭 이동하기")
    void tabTest() {
        // mk connection
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://google.com");
        String firstTab = driver.getWindowHandle();
        System.out.println("firstTab = " + firstTab);
        // A407B199D23024F2D789CA4225364E72

        // create new tab x 2
        driver.switchTo().newWindow(WindowType.TAB);
        String secondTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        String thirdTab = driver.getWindowHandle();
        // create new window
        driver.switchTo().newWindow(WindowType.WINDOW);
        String newWindow = driver.getWindowHandle();
        driver.close();


        // move first -> third -> second tab
        Set<String> windowHandles = driver.getWindowHandles();
        driver.switchTo().window(firstTab);
        driver.switchTo().window(thirdTab);
        driver.switchTo().window(secondTab);

        // close second tab
        driver.switchTo().window(secondTab).close();

    }

    @Test
    @DisplayName("google")
    void googleTest() {
        ChromeDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        driver.get("https://www.google.com");

        WebElement input = driver.findElement(By.cssSelector("[name='q'"));
        input.sendKeys("test");
        input.submit();

//        List<WebElement> elements = driver.findElements(By.cssSelector("#search>#rso>div"));
        List<WebElement> elements = driver.findElements(By.cssSelector("#rso > div"));
        for (WebElement element : elements) {
            List<WebElement> as = element.findElements(By.tagName("a"));
            if (as.isEmpty()) break;
            for (WebElement a : as) {
                String href = a.getAttribute("href");
                System.out.println("href = " + href);
                //
                String accessibleName = a.getAccessibleName();
                System.out.println("accessibleName = " + accessibleName);
                String text = a.getText();
                System.out.println("text = " + text);
                String ariaRole = a.getAriaRole();
                System.out.println("ariaRole = " + ariaRole);


                Point location = a.getLocation();
                System.out.println("location = " + location.x);
                Rectangle rect = a.getRect();
                System.out.println("rect = " + rect.x);

                System.out.println();


//                WebElement h3 = a.findElement(By.tagName("h3"));
//                String text = h3.getText();
//                System.out.println("text = " + text);
            }
        }
        driver.quit();
    }

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
        List<GoogleResultObj> result = new ArrayList<>();
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

                    GoogleResultObj googleResultObj = new GoogleResultObj();
                    result.add(googleResultObj);
                    googleResultObj.getTitle().add(atag.findElement(By.xpath("./h3")).getText());
                    googleResultObj.setLink(atag.getAttribute("href"));
                    List<WebElement> children = thisDiv.findElements(By.xpath("./div/div/div"));
                    int totalSize = children.size();
                    int contentIdx = totalSize - 2;
                    List<WebElement> spans = children.get(contentIdx).findElements(By.xpath("./div/span"));
                    for (WebElement span : spans) {
                        googleResultObj.getContent().add(span.getText());
                    }

                } catch (NoSuchElementException ignore) {
                }
            }
        } finally {
            driver.quit();
        }

        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("naver test")
    void naverTest() {
        String query = "test";
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://www.naver.com");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        //*[@id="query"]
        WebElement input = driver.findElement(By.xpath("//*[@id=\"query\"]"));
        input.sendKeys(query);
        //button[@type="submit"]
        driver.findElement(By.xpath("//button[@type=\"submit\"]")).click();


        //

        //


        // 인플루언서
        List<WebElement> lis =
                driver.findElements(By.xpath(
                        "//*[@id=\"_section_influencer\"]//div/ul/li[contains(@class, \"_item\")]"
                ));

        for (WebElement li : lis) {
            WebElement titleA = li.findElement(By.xpath(".//div[@class=\"title_area\"]/a"));
            WebElement contentA = li.findElement(By.xpath(".//div[@class=\"dsc_area\"]/a"));
            String title = titleA.getText();
            String href = contentA.getAttribute("href");
            String content = contentA.getText();
            System.out.println("title = " + title);
            System.out.println("href = " + href);
            System.out.println("content = " + content);
        }
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
