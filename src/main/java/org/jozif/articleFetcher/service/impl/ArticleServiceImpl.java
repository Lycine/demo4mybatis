package org.jozif.articleFetcher.service.impl;

import com.itextpdf.text.pdf.PdfReader;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jozif.articleFetcher.dao.ArticleMapper;
import org.jozif.articleFetcher.entity.Article;
import org.jozif.articleFetcher.entity.ArticleExample;
import org.jozif.articleFetcher.service.ArticleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.*;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service(value = "ArticleService")
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper; //这里会报错，但是并不会影响

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String Sender; //读取配置文件中的参数

    @Override
    public Article insertArticle(Article article) throws IOException {
        articleMapper.insert(article);
        return article;
    }

    //生成文件路径
    private static String path = "";

    //文件路径+名称
    private static String filenameTemp;

    @Override
    public Article findNewestTucao() {
        ArticleExample articleExample = new ArticleExample();
        ArticleExample.Criteria criteria = articleExample.createCriteria();
        criteria.andArticleNameLike("吐槽");
        List<Article> article1 = articleMapper.selectByExample(articleExample);
        if (article1.size() > 0) {
            return article1.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Article> findByArticleId(Article article) {
        ArticleExample articleExample = new ArticleExample();
        ArticleExample.Criteria criteria = articleExample.createCriteria();
        criteria.andArticleIdEqualTo(article.getArticleId());
        List<Article> article1 = articleMapper.selectByExample(articleExample);
        return article1;
    }

    @Override
    public void getArticleDetail(Article article,String isGenerateScreenshot, String isGeneratePdf,String isGenerateHtml) throws Exception {

            String url = article.getArticleUrl();
            Document doc = null;
            doc = Jsoup.connect(url).get();
            //提取html body文本内容
            article.setContent(doc.body().text());
            article.setWordCount(article.getContent().length());
            String now = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
            String fileName = article.getPlatform() + "-" + DigestUtils.md5Hex(doc.html()).substring(0, 6) + "-" + now;
        if (StringUtils.equalsAnyIgnoreCase(isGenerateHtml,"yes","true","1")){
            createFile(fileName + ".html", doc.html(), false);
            article.setHtmlPath(filenameTemp);
        } else {
            log.info("配置文件中选择不生成html");
        }

        int pageSize = 1;
        float pageHeight = 450;
        //生成去掉无用内容的html

        doc.select("body > div.main-wrap.content-wrap > div.qr").remove();
        doc.select("body > div.global-header").remove();
        createFile("tempToGenPdfOrImg.html", doc.html().replace("href=\"/", "href=\"http://static.daily.zhihu.com/").replace("src=\"/", "src=\"http://static.daily.zhihu.com/"), true);

        if (StringUtils.equalsAnyIgnoreCase(isGeneratePdf,"yes","true","1")){
            //生成pdf
            String genPdfFileName = fileName + ".pdf";

            String[] cmdGenPdf = new String[]{"google-chrome-stable", "--headless", " --disable-gpu", "--print-to-pdf=" + genPdfFileName, "tempToGenPdfOrImg.html"};
            if (!runCmd(cmdGenPdf)) {
                log.info("生成pdf失败");
                ArticleExample articleExample = new ArticleExample();
                ArticleExample.Criteria criteria = articleExample.createCriteria();
                criteria.andArticleIdEqualTo(article.getArticleId());
                articleMapper.updateByExampleSelective(article,articleExample);
                log.info("更新记录: " + article.toString());
                throw new Exception("生成pdf失败");
            }

            log.info("生成pdf成功");
            article.setPdfPath(genPdfFileName);

            //获取pdf页数,高度，计算图片高度
            PdfReader reader;
            reader = new PdfReader(genPdfFileName);
            pageSize = reader.getNumberOfPages(); //页码
//        pageHeight = reader.getPageSize(1).getHeight(); //每页高度
            reader.close();
        } else {
            log.info("配置文件中选择不生成pdf");
        }

        if (StringUtils.equalsAnyIgnoreCase(isGenerateScreenshot,"yes","true","1")){

            //生成img
            String genImgFileName = fileName + ".png";
//        google-chrome-stable
//        google-chrome-stable
//        String[] cmdGenImg = new String[]{"google-chrome-stable", "--headless", " --disable-gpu", "--screenshot=" + genImgFileName, "--window-size=600," + pageSize * 1000 + "", "tempToGenPdfOrImg.html"};
            String[] cmdGenImg = new String[]{"google-chrome-stable", "--headless", " --disable-gpu", "--screenshot=" + genImgFileName, "--window-size=1920,1680", "tempToGenPdfOrImg.html"};

            if (!runCmd(cmdGenImg)) {
                log.info("生成img失败");
                ArticleExample articleExample = new ArticleExample();
                ArticleExample.Criteria criteria = articleExample.createCriteria();
                criteria.andArticleIdEqualTo(article.getArticleId());
                articleMapper.updateByExampleSelective(article,articleExample);
                log.info("更新记录: " + article.toString());
                throw new Exception("生成img失败");
            }
            log.info("生成img成功");
            article.setScreenshotPath(genImgFileName);
        } else {
            log.info("配置文件中选择不生成img");
        }


        ArticleExample articleExample = new ArticleExample();
        ArticleExample.Criteria criteria = articleExample.createCriteria();
        criteria.andArticleIdEqualTo(article.getArticleId());
        articleMapper.updateByExampleSelective(article,articleExample);
        log.info("更新记录: " + article.toString());
    }


    public boolean runCmd(String[] cmd) {
        try {
            //      logger.info("启用失败或删除wifi后删除连接信息:"+cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            //   StringBuilder result = new StringBuilder();
            log.info("========runCmd start=======");
            log.info("cmd: " + StringUtils.join(Arrays.asList(cmd), " "));
            while ((line = br.readLine()) != null) {
                log.info(line);
            }
            log.info("========runCmd end=======");
            int exitValue = p.waitFor();

            log.info("exitValue: " + String.valueOf(exitValue));

            return exitValue == 0;
        } catch (Exception e) {
            log.error("Something's wrong here");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 创建文件
     *
     * @param fileName    文件名称
     * @param filecontent 文件内容
     * @return 是否创建成功，成功则返回true
     */
    public static boolean createFile(String fileName, String filecontent, boolean isCover) {
        Boolean bool = false;
        filenameTemp = path + fileName;//文件路径+名称+文件类型
        File file = new File(filenameTemp);
        try {
            //如果文件不存在，则创建新的文件
            boolean willCreate = false;
            if (isCover) {
                willCreate = true;
            } else {
                if (!file.exists()) {
                    willCreate = true;
                }
            }
            if (willCreate) {
                file.createNewFile();
                bool = true;
                log.info("success create file,the file is " + filenameTemp);
                //创建文件成功后，写入内容到文件里
                writeFileContent(filenameTemp, filecontent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bool;
    }

    /**
     * 向文件中写入内容
     *
     * @param filepath 文件路径与名称
     * @param newstr   写入的内容
     * @return
     * @throws IOException
     */
    public static boolean writeFileContent(String filepath, String newstr) throws IOException {
        Boolean bool = false;
        String filein = newstr + "\r\n";//新写入的行，换行
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            //文件原有内容
            for (int i = 0; (temp = br.readLine()) != null; i++) {
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return bool;
    }

//    @Override
//    public int sendMail(String to, String subject, String content) {
//        MimeMessage message = null;
//        try {
//            message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setFrom(Sender);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(content, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mailSender.send(message);
//    }

    @Override
    public List<Article> getHomePageOfDailyZhihuStoryList() throws IOException {
        Document doc = null;
        doc = Jsoup.connect("http://daily.zhihu.com/").get();
        Elements passages = doc.select(".box");
        List<Article> articles = new ArrayList<>();
        for (Element passage : passages) {
            Article article = new Article();
            String title = passage.select(".title").text();
            String previewImg = passage.select(".preview-image").attr("src");
            String url = passage.select(".link-button").attr("href");
            String[] urlSplittedArray = url.split("/");
            String passageIdString = urlSplittedArray[urlSplittedArray.length - 1];
            article.setArticleName(title);
            article.setRemark("previewImg: " + previewImg + ";");
            article.setArticleId("zhihu-" + passageIdString);
            article.setPlatform("zhihu");
            article.setArticleUrl("http://daily.zhihu.com" + url);
            articles.add(article);
//            if (null != title && title.contains("瞎扯")) {
//                String[] linkUrl = passage.select(".link-button").attr("href").split("/");
//                String passageIdString = linkUrl[linkUrl.length - 1];
//                if (Integer.parseInt(passageIdString) > passageId) {
//                    passageId = Integer.parseInt(passageIdString);
//                    break;
//                }
//            }
        }
//        "http://daily.zhihu.com/story/" + passageId
        return articles;
    }

    @Async
    public void sendMail(String to, String subject, String content) {
        MimeMessage message = null;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(Sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(message);
    }
}