package com.ise.project.sentiAnalysis;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vishwa on 4/5/2016.
 */
public class Preprocessing {
    @Autowired
    PositiveWordsArray positiveWordsArray;



    public static void main(String args[]) throws  Exception {

//        Splitter list = new ArrayList<>();
//        String s = "RT @vidyarthee: \"#RahulGandhi  accept hits out at #Modi and above below, accuses both of killing #democracy\" https://t.co/jVime4SUDS @IndiaToday https:/…";
    String s = "not below above ";
//        try (BufferedReader br =
//                     new BufferedReader(new FileReader("C:\\Users\\vishwa\\Desktop\\twitter-analysis-mastespringboot\\core\\src\\main\\resources\\TweetData"))) {
//           while((s = br.readLine())!=null)
//
//            System.out.println(s);
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found");
//        }
//          list.add(0,s);

//        System.out.println(list.get(0));
        List<String> list = new ArrayList<>();
//            list= com.google.common.base.Splitter.on(" ").trimResults();
        System.out.println(list);
        System.out.println("**************"+Processor.sanitizeText(s));
        Iterator<String> iterator = Splitter.on(CharMatcher.anyOf("  @#:!.,\"")).omitEmptyStrings().split(s).iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

//        System.out.println(list);
//        list.stream().forEach(System.out::println);


        List<String> finalList = new ArrayList<>();

        finalList = list.stream().filter(Preprocessing::isConfirm).collect(Collectors.toList());

//            finalList.stream().map(SentiWordList::getValue).forEach(System.out::println);

//        System.out.println(finalList);
//        Integer sum = SentiWordList.getValue("abandon");
//        System.out.println(sum);
 Integer sum=0;
        System.out.println("--------------------------------\n");

//        finalList.stream().forEach(System.out::println);
        System.out.println("-------------------------\n");

//        Iterator iterator1 = finalList.iterator();
        System.out.println("count is --");
        System.out.println(finalList.stream().mapToInt(SentiWordList::getValue).sum());
        System.out.println("count is --");
//        System.out.println(finalList.stream()
//        try{
//
//            for (String ss: finalList)
//            {
//              sum+=  SentiWordList.getValue(ss);
//                System.out.println(sum);
//            }
//
//
//    }catch (Exception e)
//        {
//            System.out.println("error " + e);
//        }
//

//        System.out.println("------------------------\n" + "Total score " + sum);
     }
        public static boolean isConfirm(String s)
        {
            NegationWords.getAntonyms();
            PositiveWordsArray positiveWordsArray=new PositiveWordsArray();
            NegationWords negationWords= new NegationWords();
            NegativeWordArray negativeWordArray= new NegativeWordArray();
            return positiveWordsArray.isPositive(s) || negativeWordArray.isNegative(s) || NegationWords.isNegation(s) ;
        }

    }



