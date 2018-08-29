package org.jozif.articleFetcher.controller;


import io.swagger.annotations.ApiParam;
import lombok.extern.java.Log;

import lombok.extern.slf4j.Slf4j;
import org.jozif.articleFetcher.entity.Article;
import org.jozif.articleFetcher.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64;

@Slf4j
@Controller
@RequestMapping(value = "/artical")
public class UserController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private @Value("${spring.mail.username}")
    String sender;

    @ResponseBody
    @RequestMapping(value = "/sendTucaoMail/{receiver}", produces = {"application/json;charset=UTF-8"})
    public int addUser(@PathVariable("receiver") String receiver) throws Exception {
        log.info("encode base64: " + receiver);
        String decodedBase64 = new String(Base64.decodeBase64(receiver));
        log.info("decodedBase64: " + decodedBase64);

        Article article = articleService.findNewestTucao();
        if (null == article) {
            log.warn("未找到最新的瞎扯,不发送邮件");
            return 1;
        }
        log.info("article1: " + article.toString());
        articleService.getArticleDetailForTucao(article);
        article = articleService.findNewestTucao();
        if (null == article) {
            log.warn("未找到最新的瞎扯,不发送邮件");
            return 1;
        }
        log.info("article2: " + article.toString());

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        sendArticleMail(decodedBase64, "瞎扯|" + time, article);
        log.info("success, send to decodedBase64: " + decodedBase64 + ", time: " + time + ", articleId" + article.getArticleId());

        log.info("all success!");
        return 0;
    }

    private void sendArticleMail(String to, String subject, Article article) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //基本设置.
        helper.setFrom(sender);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText("<body><img src='cid:"+article.getArticleId()+"' />原文链接: " + article.getArticleUrl() + "</body>", true);
        // 邮件内容，第二个参数指定发送的是HTML格式
        //说明：嵌入图片<img src='cid:head'/>，其中cid:是固定的写法，而aaa是一个contentId。

        FileSystemResource file = new FileSystemResource(new File(article.getScreenshotPath()));
        helper.addInline(article.getArticleId(), file);
        mailSender.send(mimeMessage);
    }
}