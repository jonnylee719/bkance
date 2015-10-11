package com.simpleastudio.recommendbookapp.api;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.simpleastudio.recommendbookapp.Book;
import com.simpleastudio.recommendbookapp.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;

/**
 * Created by Jonathan on 7/10/2015.
 */
public class GoodreadsFetcher {
    private static final String TAG = "GoodreadsFetcher";
    private Context mAppContext;
    private Book mBook;
    private static final String ENDPOINT = "https://www.goodreads.com/search.xml?";
    private static final String PARAM_SEPARATOR = "&";
    private static final String SPACE_ENCODED = "%20";
    //Parameters
    private static final String PARAM_QUERY = "q=";
    private static final String PARAM_PAGE = "page=";
    private static final String PARAM_SEARCH_TITLE = "searchtitle=";
    private static final String PARAM_FORMAT = "format=";
    private static final String PARAM_TITLE = "title=";
    private static final String PARAM_AUTHOR = "author=";
    private static final String PARAM_RATING = "rating=";
    private static final String PARAM_API = "key=";
    //Default parameters
    private static final String DEFAULT_FORMAT = "xml";

    //XML Parser
    private static final String XML_DAY = "original_publication_day";
    private static final String XML_MONTH = "original_publication_month";
    private static final String XML_YEAR = "original_publication_year";
    private static final String XML_RATING_COUNT = "ratings_count";
    private static final String XML_RATING = "average_rating";
    private static final String XML_THUMB = "image_url";
    private static final String XML_WORK = "work";
    private static final String XML_AUTHOR = "name";
    private static final String XML_BOOK = "best_book";
    private static final String XML_ID = "id";

    public GoodreadsFetcher(Context c){
        mAppContext = c;
    }

    public Book getBookInfo(Book book){
        String title = book.getmTitle();
        String urlString = ENDPOINT
                + PARAM_QUERY + getSpaceEncoded(title) + PARAM_SEPARATOR
                + PARAM_SEARCH_TITLE + getSpaceEncoded(title) + PARAM_SEPARATOR
                + PARAM_API + mAppContext.getResources().getString(R.string.goodreads);

        try{
            String responseString = getResponse(urlString);
            Log.d(TAG, "responseString: " + responseString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(responseString));
            book = parseXmlResponse(book, parser);

            return book;
        }catch (IOException ie){
            Log.e(TAG, "IOException: " + ie);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return book;
    }

    public String getSpaceEncoded(String in){
        return in.replace(" ", SPACE_ENCODED);
    }


    public JSONObject parseJsonResponse(String responseString){
        try{
            JSONObject results = new JSONObject(responseString);
            return results;
        } catch (JSONException je) {
            Log.e(TAG, "JsonException: " + je);
        }
        return null;
    }

    public Book parseXmlResponse(Book book, XmlPullParser parser) throws XmlPullParserException, IOException{

            Log.d(TAG, "Started parsing xml Response.");
            int eventType = parser.next();
            boolean stopParsing = false;
            while(eventType != XmlPullParser.END_DOCUMENT && !stopParsing){
                if(eventType == XmlPullParser.START_TAG &&
                        XML_DAY.equals(parser.getName())){
                    String day = parser.nextText();
                    Log.d(TAG, "Day: " + day);
                    if(!day.equals(""))
                        book.setmDay(Integer.parseInt(day));
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_MONTH.equals(parser.getName())){
                    String month = parser.nextText();
                    Log.d(TAG, "Month: " + month);
                    if(!month.equals(""))
                        book.setmMonth(Integer.parseInt(month));
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_YEAR.equals(parser.getName())){
                    String year = parser.nextText();
                    Log.d(TAG, "Year: " + year);
                    if(!year.equals(""))
                        book.setmYear(Integer.parseInt(year));
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_RATING_COUNT.equals(parser.getName())){
                    String ratingCount = parser.nextText();
                    Log.d(TAG, "Rating count: " + ratingCount);
                    if(!ratingCount.equals(""))
                        book.setmRatingCount(Integer.parseInt(ratingCount));
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_RATING.equals(parser.getName())){
                    String rating= parser.nextText();
                    Log.d(TAG, "Rating: " + rating);
                    if(!rating.equals(""))
                        book.setmAvgRating(Double.parseDouble(rating));
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_THUMB.equals(parser.getName())){
                    String url = parser.nextText();
                    Log.d(TAG, "Url: " + url);
                    if(!url.equals(""))
                        book.setmThumbnailUrl(url);
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_AUTHOR.equals(parser.getName())){
                    String author = parser.nextText();
                    Log.d(TAG, "Author: " + author);
                    if(!author.equals(""))
                        book.setmAuthors(author);
                }
                else if(eventType == XmlPullParser.START_TAG &&
                        XML_ID.equals(parser.getName())){
                    String id = parser.nextText();
                    if(!id.equals("") && book.getmId()==null)
                        book.setmId(id);
                    Log.d(TAG, "id: " + book.getmId());
                }
                else if(eventType == XmlPullParser.END_TAG &&
                        XML_WORK.equals(parser.getName())){
                    stopParsing = true;
                }
                eventType = parser.next();
            }
        return book;
    }

    public String getResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        Log.d(TAG, "url: " + url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);                //5 seconds of waiting for input stream read
            connection.setConnectTimeout(5000);             //5 seconds of waiting for connection

            String responseMessage = connection.getResponseMessage();
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                Log.d(TAG, "GoodreadsAPI request failed. Response Code: " + responseCode +
                        "Response Message: " + responseMessage);
                connection.disconnect();
                return null;
            }
            Log.d(TAG, "GoodreadsAPI request was successful. Response Code: " + responseCode +
                    "Response Message: " + responseMessage);

            //Read data from response
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            return builder.toString();
        } catch (SocketTimeoutException se){
            Log.e(TAG, "SocketTimeoutException: " + se);
        }finally {
            connection.disconnect();
        }
        return null;
    }

}
