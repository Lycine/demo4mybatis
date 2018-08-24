package org.jozif.articleFetcher.service;

import org.jozif.articleFetcher.entity.Article;

import java.io.IOException;
import java.util.List;

public interface ArticleService {

//    int add(Article article);

    Article insertArticle(Article article) throws IOException;

    Article findNewestTucao();

    List<Article> findByArticleId(Article article);

    void getArticleDetail(Article article,String isGenerateScreenshot, String isGeneratePdf,String isGenerateHtml) throws Exception;

    List<Article> getHomePageOfDailyZhihuStoryList() throws IOException;
}
