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

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

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



    //    @Scheduled(cron = "0 10 7 * * ? ")
//    @Scheduled(cron = "0 0 0/3 * * ? ")
//    @Scheduled(cron = "0 0 0/1 * * ? ")
    @Scheduled(cron = "0  0/1 * * * ? ")
    public void timerToNow() throws Exception {
        List<Article> articleList = articleService.getHomePageOfDailyZhihuStoryList();
        for (Article article : articleList) {
            if (articleService.findByArticleId(article).size() == 0) {
                articleService.insertArticle(article);
                articleService.getArticleDetail(article, isGenerateScreenshot, isGeneratePdf, isGenerateHtml);
            }
        }
    }


    @Scheduled(cron = "0 10 7 * * ? ")
    public void sendMail() throws Exception {
        Article article = articleService.findNewestTucao();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //基本设置.
        helper.setFrom("412887952@qq.com");//发送者.
        helper.setTo("1473773560@qq.com");//接收者.
        helper.setSubject("测试静态资源（邮件主题）");//邮件主题.
        // 邮件内容，第二个参数指定发送的是HTML格式
        //说明：嵌入图片<img src='cid:head'/>，其中cid:是固定的写法，而aaa是一个contentId。
        helper.setText("<body>这是图片：<img src='cid:head' /></body>", true);
        FileSystemResource file = new FileSystemResource(new File(article.getScreenshotPath()));
        helper.addInline("head",file);
        mailSender.send(mimeMessage);
    }
//
//        String content = util.getPassageHtml();
//
//        Set<String> receiversMail = new HashSet();
//        for (String receiverMail : StringUtils.split(receivers, ";")) {
//            if (!StringUtils.isEmpty(receiverMail)) {
//                receiversMail.add(receiverMail);
//            }
//        }
//
//
//        for (String receiver : receiversMail) {
//            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//            util.sendMail(receiver, "瞎扯|" + time, content);
//            log.info("success, send to receiver: " + receiver + ", time: " + time + ", content: " + content);
//        }
//        log.info("all success!");

}
