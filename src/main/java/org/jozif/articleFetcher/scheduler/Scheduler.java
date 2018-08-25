package org.jozif.articleFetcher.scheduler;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.jozif.articleFetcher.entity.Article;
import org.jozif.articleFetcher.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class Scheduler {
    @Value("${receivers}")
    private String receivers; //读取配置文件中的参数

    @Value("${isGenerateScreenshot}")
    private String isGenerateScreenshot;

    @Value("${isGeneratePdf}")
    private String isGeneratePdf;

    @Value("${isGenerateHtml}")
    private String isGenerateHtml;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender; //读取配置文件中的参数

    //        @Scheduled(cron = "0 10 7 * * ? ")
//    @Scheduled(cron = "0 0 0/3 * * ? ")
//    @Scheduled(cron = "0 0 0/1 * * ? ")
//    @Scheduled(cron = "0  2/5 * * * ? ")
    @Scheduled(cron = "0  3/20 * * * ?")
    public void timerToNow() throws Exception {
        log.info("开始抓取文章");
        List<Article> articleList = articleService.getHomePageOfDailyZhihuStoryList();
        for (Article article : articleList) {
            if (articleService.findByArticleId(article).size() == 0) {
                log.info("该文章不存在，正在抓取，articleId: " + article.getArticleId());
                articleService.insertArticle(article);
                articleService.getArticleDetail(article, isGenerateScreenshot, isGeneratePdf, isGenerateHtml);
            } else {
                log.info("该文章已存在");
            }
        }
        log.info("抓取文章结束");
    }

    @Scheduled(cron = "0 10 7 * * ? ")
    public void sendTucaoMail() throws Exception {
        Article article = articleService.findNewestTucao();
        if (null == article) {
            log.warn("未找到最新的瞎扯,不发送邮件");
            return;
        }
        log.info("article1: " + article.toString());
        articleService.getArticleDetail(article, "true", isGeneratePdf, isGenerateHtml);
        article = articleService.findNewestTucao();
        if (null == article) {
            log.warn("未找到最新的瞎扯,不发送邮件");
            return;
        }
        log.info("article2: " + article.toString());
        Set<String> receiversMail = new HashSet();
        for (String receiverMail : StringUtils.split(receivers, ";")) {
            if (!StringUtils.isEmpty(receiverMail)) {
                receiversMail.add(receiverMail);
            }
        }

        for (String receiver : receiversMail) {
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            sendArticleMail(receiver, "瞎扯|" + time, article);
            log.info("success, send to receiver: " + receiver + ", time: " + time + ", articleId" + article.getArticleId());
        }
        log.info("all success!");
    }

    private void sendArticleMail(String to, String subject, Article article) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //基本设置.
        helper.setFrom(sender);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText("<body><img src='cid:head' />原文链接" + article.getArticleUrl() + "</body>", true);
        // 邮件内容，第二个参数指定发送的是HTML格式
        //说明：嵌入图片<img src='cid:head'/>，其中cid:是固定的写法，而aaa是一个contentId。

        FileSystemResource file = new FileSystemResource(new File(article.getScreenshotPath()));
        helper.addInline("head", file);
        mailSender.send(mimeMessage);
    }
}
