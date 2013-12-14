package com.agcy.reader.core;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agcy.reader.R;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class Parser{
    private String xmlString = "empty";
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    //public RssChannel response = null;
    static Context context;
    public static void initialization(Context context){
        Parser.context = context;
    }
    public String status = "empty";
    public static View parseHtml(String html){
        final float scale = context.getResources().getDisplayMetrics().widthPixels;
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1000, 1000);

        LinearLayout parsedView = new LinearLayout(context);
        parsedView.setOrientation(LinearLayout.VERTICAL);
        //parsedView.setLayoutParams(params);
        TextView text = new TextView(context);
        text.setText("test start \b");






        TextView text2 = new TextView(context);
        text2.setText(Html.fromHtml(html,imgGetter,null));

        text2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView text3 = new TextView(context);
        text3.setText("test end \b");

        //text.setLayoutParams(params);
        parsedView.addView(text);
        parsedView.addView(text2);
        parsedView.addView(text3);
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

            final float scale = context.getResources().getDisplayMetrics().widthPixels;
            Drawable drawable = null;
            drawable = context.getResources().getDrawable(R.drawable.ic_launcher);
            drawable.setBounds(0, 0, (int) scale-75, drawable
                    .getIntrinsicHeight());
            return drawable;
        }
    };
    public void execute(){
        /*
        RssChannel parseResponse;

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

                parseResponse  = new RssChannel("temp","temp","temp");
                temp= (Element)  element.getElementsByTagName("language").item(0);
                language=temp.getFirstChild().getNodeValue();

                temp= (Element)  element.getElementsByTagName("title").item(0);
                name=temp.getFirstChild().getNodeValue();

                parseResponse.title = name;
            }
            NodeList nodeList = element.getElementsByTagName("item");

            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {


                    RssItem rssItem = new RssItem();
                    Element entry = (Element) nodeList.item(i);

                    temp = (Element) entry.getElementsByTagName(
                            "title").item(0);
                    rssItem.title = temp.getFirstChild().getNodeValue();

                    temp = (Element) entry.getElementsByTagName(
                            "description").item(0);
                    rssItem.description = temp.getFirstChild().getNodeValue();

                    temp = (Element) entry.getElementsByTagName(
                            "pubDate").item(0);
                    rssItem.pubDate = temp.getFirstChild().getNodeValue();

                    temp = (Element) entry.getElementsByTagName(
                            "author").item(0);
                    if (temp!=null)
                        rssItem.author = temp.getFirstChild().getNodeValue();

                    rssItem.channel = name;
                    rssItem.language = language;
                    parseResponse.items.add(rssItem);
                }
            }
            status = "success";
            response= parseResponse;
        } catch (SAXException e) {
            status= "Rss error";
        } catch (IOException e) {
            status= "Rss error";
        } catch (ParserConfigurationException e) {
            status = "Parser error";
        }
    }
    public Parser(String xmlString){
        this.xmlString = xmlString;
    }*/
    }
}
