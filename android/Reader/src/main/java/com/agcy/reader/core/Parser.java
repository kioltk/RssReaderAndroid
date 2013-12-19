package com.agcy.reader.core;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agcy.reader.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class Parser{
    private String xmlString = "empty";
    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static DocumentBuilder builder = null;
    //public RssChannel response = null;
    static Context context;
    public static void initialization(Context context){
        Parser.context = context;
    }
    public String status = "empty";
    public static View parseHtml(String html){

        final float scale = context.getResources().getDisplayMetrics().widthPixels;

        LinearLayout parsedView = new LinearLayout(context);
        parsedView.setOrientation(LinearLayout.VERTICAL);
        parsedView.setDividerPadding(0);
        //parsedView.setLayoutParams(params);





        //TextView text = new TextView(context);
        //text.setText("test start \b");






        //TextView text2 = new TextView(context);
        //text2.setText(Html.fromHtml(html,imgGetter,null));

        //text2.setText(html);
        try{

            /*
            builder = factory.newDocumentBuilder();
            Document document = null;
            StringReader sr = new StringReader(html.replaceAll("&", "&amp;"));
            InputSource inputSource = new InputSource(sr);

            document = builder.parse(inputSource);
            Element allDoc = document.getDocumentElement();
            NodeList imageTags = allDoc.getElementsByTagName("img");
            */

            String[] imageTags = html.split("<img");
            String textTags = html;
            try{
                //textTags = html.split("<img ([A-Za-z=\"'+-:/\\. ])+>");
            }catch (Exception exp){
                Log.e("agcylog","ошибка регекса "+exp.getMessage());
            }

            while (textTags.length()!=0){

                int imgPosition = textTags.indexOf("<img");
                switch (imgPosition)
                {
                    case -1:{
                        imgPosition = textTags.length();
                    }
                    default:{
                        String textTag = textTags.substring(0, imgPosition);
                        textTags = textTags.substring(imgPosition);
                        Spanned currentText = Html.fromHtml(textTag, null, null);
                        TextView textView1 = new TextView(context);
                        textView1.setText(currentText);
                        textView1.setMovementMethod(LinkMovementMethod.getInstance());
                        parsedView.addView(textView1);
                    }break;
                    case 0:{
                        imgPosition = textTags.indexOf(">");
                        String imageTag = textTags.substring(0, imgPosition);
                        textTags = textTags.substring(imgPosition+1);
                        ImageView imageView = new ImageView(context);
                        int tagStartIndex = imageTag.indexOf("src=\"")+5;

                        String substringedStartTag = imageTag.substring(tagStartIndex);
                        int sourceEndIndex = substringedStartTag.indexOf("\"");
                        String substringedTag = substringedStartTag.substring(0, sourceEndIndex);
                        Imager.setImageUrl(substringedTag, imageView);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        params.setMargins(8, 0, 0, 8);
                        imageView.setLayoutParams(params);
                        //textView.setText(substringedTag);
                        parsedView.addView(imageView);
                        //parsedView.addView(textView);
                    }break;
                }

            }
            /*
            for(int i = 0; i <imageTags.length;i++){
                TextView textView = new TextView(context);
                ImageView imageView = new ImageView(context);
                String imageTag = imageTags[i];
                try{
                    if(imageTag!=null && !imageTag.equals("") && !imageTag.equals("<p>")){

                        int tagStartIndex = imageTag.indexOf("src=\"")+5;
                        int tagEndIndex = imageTag.indexOf(">");
                        String substringedStartTag = imageTag.substring(tagStartIndex, tagEndIndex);
                        int sourceEndIndex = substringedStartTag.indexOf("\"");
                        String substringedTag = substringedStartTag.substring(0, sourceEndIndex);
                        Imager.setImageUrl(substringedTag, imageView);
                        textView.setText(substringedTag);
                        parsedView.addView(imageView);
                        //parsedView.addView(textView);

                        //Element imageTag = (Element) imageTags.item(i);
                        //String source = imageTag.getAttribute("src");
                        //ImageView imageView = new ImageView(context);
                        //Imager.setImage(source,imageView);
                        //parsedView.addView(imageView);
                }
                }catch (Exception exp){

                }
            }
            */


        }catch (Exception exp){
            Log.i("agcylog","ошибка парсера " + exp.getMessage());
        }

        //TextView text3 = new TextView(context);
        //text3.setText("test end \b");

        //text.setLayoutParams(params);
        //parsedView.addView(text);
        //parsedView.addView(text2);
        //parsedView.addView(text3);
        //LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /*View v = vi.inflate(R.layout.tile_entry, null);

        // fill in any details dynamically here
        TextView textView = (TextView) v.findViewById(R.id.entryFullContent);
        textView.setText("your text");

        // insert into main view
        //View insertPoint = findViewById(R.id.insert_point);
        ((ViewGroup) parsedView).addView(v, 0, params);
        */



        return parsedView;
    }
    private static Html.ImageGetter imgGetter = new Html.ImageGetter() {

        public Drawable getDrawable(String source) {
            final float density = context.getResources().getDisplayMetrics().density;
            int padding = (int) (37.5 * density + 0.5f + 2);
            final float scale = context.getResources().getDisplayMetrics().widthPixels;
            Drawable drawable = null;

            drawable = context.getResources().getDrawable(R.drawable.loading);
            drawable.setBounds(0, 0, (int) scale - padding, drawable
                    .getIntrinsicHeight());
            return drawable;
        }
    };

    public static ArrayList<String> fetchImages(String content){
        final ArrayList<String> images = new ArrayList<String>();
        Html.ImageGetter fetcher = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                images.add(source);
                return  null;
            }
        };
        Html.fromHtml(content,fetcher,null);
        return images;
    }
    public void execute(){


        try {
            builder = factory.newDocumentBuilder();

            Document document = null;
            document = builder.parse(new InputSource(new StringReader(xmlString)));


            Element element = document.getDocumentElement();
            Element temp;
            String rssCheck = element.getTagName();
            if(!rssCheck.equals("rss"))
            {
                status="It's not rss!";
                return;
            }
            String language;
            String name;
            {
               Element channel = (Element) element.getElementsByTagName("channel").item(0);

                //parseResponse  = new RssChannel("temp","temp","temp");
                temp= (Element)  element.getElementsByTagName("language").item(0);
                language=temp.getFirstChild().getNodeValue();

                temp= (Element)  element.getElementsByTagName("title").item(0);
                name=temp.getFirstChild().getNodeValue();

                //parseResponse.title = name;
            }
            NodeList nodeList = element.getElementsByTagName("item");

            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {


             //       RssItem rssItem = new RssItem();
                    Element entry = (Element) nodeList.item(i);

                    temp = (Element) entry.getElementsByTagName(
                            "title").item(0);
               //     rssItem.title = temp.getFirstChild().getNodeValue();

                    temp = (Element) entry.getElementsByTagName(
                            "description").item(0);
                 //   rssItem.description = temp.getFirstChild().getNodeValue();

                    temp = (Element) entry.getElementsByTagName(
                            "pubDate").item(0);
//                    rssItem.pubDate = temp.getFirstChild().getNodeValue();

                    temp = (Element) entry.getElementsByTagName(
                            "author").item(0);
        //            if (temp!=null)
  //                      rssItem.author = temp.getFirstChild().getNodeValue();

    //                rssItem.channel = name;
      //              rssItem.language = language;
        //            parseResponse.items.add(rssItem);
                }
            }
        } catch (SAXException e) {
            status= "Rss error";
        } catch (IOException e) {
            status= "Rss error";
        } catch (ParserConfigurationException e) {
            status = "Parser error";
        }
    }

}
