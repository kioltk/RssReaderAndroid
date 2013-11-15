package com.agcy.reader.core;

import com.agcy.reader.Models.RssChannel;
import com.agcy.reader.Models.RssItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class Parser{
    private String xmlString = "empty";
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    public RssChannel response = null;
    public String status = "empty";
    public void execute(){

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
    }
}
