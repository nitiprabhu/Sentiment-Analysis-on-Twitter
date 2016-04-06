package com.ise.project.sentiAnalysis;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by nitish 01/03/2016.
 */
@Configuration
public class Processor {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    public static String URL_REGEX = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.," +
            "@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
    private static int TWEETS_QUANTITY = 10;
    SendRequest sendRequest;

    private String DATUMBOX_API_KEY;
    private String DATUMBOX_API_ENDPOINT;
    private String TWITTER_APP_ID;
    private String TWITTER_APP_SECRET;
    private String TWITTER_ACCESS_TOKEN;
    private String TWITTER_ACCESS_TOKEN_SECRET;
    public List<String> list = new  ArrayList();
    @Autowired
    @Qualifier("myProperties")
    private Properties myProps;
    private String[] trends = {"#NarendraModi"};
    @Bean
    public Processor getProcessor() {
        sendRequest = new SendRequest();
        initProperties();
        return this;
    }

    @Bean(name = "myProperties")
    public Properties getMyProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/application.properties"));
    }

    private void initProperties() {
        this.TWITTER_APP_ID = myProps.getProperty("spring.social.twitter.appId");
        this.TWITTER_APP_SECRET = myProps.getProperty("spring.social.twitter.appSecret");
        this.TWITTER_ACCESS_TOKEN = myProps.getProperty("spring.social.twitter.access.token");
        this.TWITTER_ACCESS_TOKEN_SECRET = myProps.getProperty("spring.social.twitter.access.token.secret");
        this.DATUMBOX_API_KEY = myProps.getProperty("datumbox.api.key");
        this.DATUMBOX_API_ENDPOINT = myProps.getProperty("datumbox.api.endpoint");
    }

    public void analyzeTrends() throws Exception {
        System.out.println("Starting to analyze...");
        System.out.println("Asking for " + TWEETS_QUANTITY + " number of tweets per query.");
        int neutrals = 0;
        for (String trend : trends) {
            System.out.println("Analyzing: " + trend);
            neutrals += run(trend);
        }



        System.out.println("API performance:ha " + 100 * ((float) neutrals / (float) (TWEETS_QUANTITY * trends
                .length)) + "%");
    }

    private Integer run(String input) throws Exception {
        Twitter twitter = new TwitterTemplate(TWITTER_APP_ID, TWITTER_APP_SECRET, TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET);




        SearchResults searchResults = twitter.searchOperations().search(input, TWEETS_QUANTITY);
        int result = 0;
        int negative = 0;
        int neutral = 0;

        int positive = 0;

        for (Tweet tweet : searchResults.getTweets()) {
            int temp = 0;

            try {
                if (tweet != null && tweet.getText() != null) {
                    result += getDatumBoxValuation(sanitizeText(tweet.getText()));

                   System.out.println("Tweet - " + tweet.getText()) ;

     //               list.add(tweet.getText());
     //                   writer
                      //writer.write(tweet.getText() + "\n");


                    temp = getDatumBoxValuation(sanitizeText(tweet.getText()));
                    switch (temp) {
                        case -1:
                            negative++;
                            break;
                        case 0:
                            neutral++;
                            break;
                        case 1:
                            positive++;
                            break;
                    }
                    result += temp;
                    System.out.print(".");
                }
            } catch (Exception e) {
                //System.out.println("error = " + e );
            }
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("C:\\Users\\vishwa\\Desktop\\twitter-analysis-mastespringboot\\core\\src\\main\\resources\\SwachBharath2"), "utf-8"))) {

//            System.out.println(list.stream().distinct().count());
                list.stream().distinct().forEach(S -> {
                    try {
                        getDatumBoxValuation(S);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        }
        System.out.println();
        System.out.println(input + " = " + result);
        System.out.println("Total analyzed for " + input + " = " + searchResults.getTweets().size());
        System.out.println("Negatives for " + input + " = " + negative);
        System.out.println("Neutrals for " + input + " = " + neutral);
        System.out.println("Positives for " + input + " = " + positive);
        return neutral;
    }

    public static String sanitizeText(String input) {
        return input.replaceAll(URL_REGEX, "");
    }

    /**
     * @param text
     * @return -1 if negative valuation
     * 0 if neutral valuation
     * 1 if positive valuation
     * @throws IOException
     */
    public int getDatumBoxValuation(String text) throws IOException {
        URL url = new URL(DATUMBOX_API_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String parameters = "api_key=" + DATUMBOX_API_KEY + "&text=" + URLEncoder.encode(text, "UTF-8");
        InputStream inputStream = sendRequest.postURL(connection, url, parameters, DATUMBOX_API_ENDPOINT);

        String jsonTxt = IOUtils.toString(inputStream);
        String responseData = parse(jsonTxt).replace("\"", "");
        //System.out.println("responseData = " + responseData);
        int response = 0;
        switch (responseData) {
            case "negative":
                response = -1;
                break;
            case "neutral":
                response = 0;
                break;
            case "positive":
                response = 1;
                break;
        }
        return response;
    }

    private String parse(String jsonLine) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("output");
        String result = jobject.get("result").toString();
        return result;
    }
}
