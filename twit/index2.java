
package twit;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
//import twitter4j.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;


public class index2 
{
	static class Pair implements Comparable<Pair> {
	    public final int index;
	    public final float value;

	    public Pair(int index, float value) {
	        this.index = index;
	        this.value = value;
	    }

	    @Override
	    public int compareTo(Pair other) {
	        //multiplied to -1 as the author need descending sort order
	        return -1 * Float.valueOf(this.value).compareTo(other.value);
	    }
	}
	public static int julianDay(int year, int month, int day) {
		  int a = (14 - month) / 12;
		  int y = year + 4800 - a;
		  int m = month + 12 * a - 3;
		  int jdn = day + (153 * m + 2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
		  return jdn;
		}
	public static int diff(int y1, int m1, int d1, int y2, int m2, int d2) {
		  return julianDay(y1, m1, d1) - julianDay(y2, m2, d2);
		}

	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, org.apache.lucene.queryparser.classic.ParseException, java.text.ParseException 
	{
		Analyzer analyzer = new StandardAnalyzer();

        // Store the index in memory:
//        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
		Directory directory = FSDirectory.open(Paths.get("/home/Joshua/Documents/indexTweets"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer).setOpenMode(OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        
        int numFiles = 1;
        for(int i = 0; i < numFiles; i++)
        {
			JSONParser parser = new JSONParser();
			try
			{
				String filePath = "/home/Joshua/Documents/tweets/numX"+i+".json";
				System.out.println("Reading " + filePath);
				JSONArray a = (JSONArray) parser.parse(new FileReader(filePath));
				for(Object o : a) //for each object in the json array
				{
					JSONObject tweet = (JSONObject) o; //create the iteration of tweet
					Document doc = new Document(); // create the document
					String name = (String) tweet.get("name");
					String username = (String) tweet.get("username");
					String text = (String) tweet.get("text");
					String date = (String) tweet.get("date");
					String title = (String) tweet.get("titles");
					title = title.replace("[","");
					title = title.replace("]","");

					String hashtag = (String) tweet.get("hashtags").toString();
					hashtag = hashtag.replace("[","");
					hashtag = hashtag.replace("]","");
					hashtag = hashtag.replace(","," ");
					hashtag = hashtag.replace("\"","");
					
				
					
					//check if tweet has geolocation, if not it will be null
					String state = "";
					String country = "";
					String lat = "";
					String longs = "";
					String hasgeo = "0"; // 0 indicates tweet doesn't have geolocation

					if ( (String) tweet.get("state") != null) {
					 hasgeo = "1"; //1 indicates tweet has geolocation
					 state = (String) tweet.get("state");
					 country = (String) tweet.get("country");
					 lat = (String) tweet.get("lat").toString();
					 longs = (String) tweet.get("long").toString();
					}

					//end get geolocation
					
					doc.add(new StringField("Title", title, Field.Store.YES));			
		            doc.add(new StringField("Name", name, Field.Store.YES));
		            doc.add(new StringField("UserName", username, Field.Store.YES));
		            doc.add(new TextField("Text", text, Field.Store.YES));
		            doc.add(new StringField("Date", date, Field.Store.YES));
		            doc.add(new TextField("Hashtags",hashtag,Field.Store.YES));
		            //hasGeo field to later search for tweets that have GeoLocation
		            doc.add(new StringField("HasGeo",hasgeo,Field.Store.YES));
		            //adds geolocation if it has it
		           
		            doc.add(new StringField("State",state,Field.Store.YES));
		            doc.add(new StringField("Country",country,Field.Store.YES));
		            doc.add(new StringField("Lat",lat,Field.Store.YES));
		            doc.add(new StringField("Long",longs,Field.Store.YES));
		            
		            
		            //Add more here
		            indexWriter.addDocument(doc);
				}
				
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
        }
        indexWriter.close();
		
		
//		// Now search the index:
//        DirectoryReader indexReader = DirectoryReader.open(directory);
//        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        
//        String[] fields = {"UserName", "Text", "Hashtags"}; //Add the fields here
//        Map<String, Float> boosts = new HashMap<>();
//        boosts.put(fields[0],  .75f);
//        boosts.put(fields[1], 1.0f);
//        boosts.put(fields[2],  1.0f);
//
//        //adjust these 
//       	MultiFieldQueryParser parser2 = new MultiFieldQueryParser(fields, analyzer, boosts);
//       	Query query = parser2.parse("BTS");
//       	int topHitCount = 100;
//       	//try to incorporate userProfiling & date relevancy
//       	
////      Sort sort = new Sort(SortField.FIELD_SCORE, new SortField("Date", SortField.Type.STRING));
//        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;
//        //add date relevance to scores
// 
//        
//        String currdate = new Date().toString(); //current date
//        String[] splitNow = currdate.split("\\s+"); // turn string to array
//    	// [0] = day, [1] = month, [2] = date, [3] = time.=, [ 5] = year
//        String split2 = splitNow[2] + ' ' + splitNow[1] + ' ' + splitNow[5];
//             
//        SimpleDateFormat format1=new SimpleDateFormat("dd MMM yyyy");
//        SimpleDateFormat format2=new SimpleDateFormat("dd MM yyyy");
//		Date currdate2=format1.parse(split2);
//        String currdate3 = format2.format(currdate2);
//        splitNow = currdate3.split("\\s+");
//
//        int d1 = Integer.parseInt(splitNow[0]); //date
//        int m1 = Integer.parseInt(splitNow[1]); // month
//        int y1 = Integer.parseInt(splitNow[2]); // year
//        
//        
//        for (int rank = 0; rank < hits.length; ++rank) {
//        	Document hitDoc = indexSearcher.doc(hits[rank].doc); // list of all the docs
//        	//System.out.println(hitDoc.get("Date")); //datetime of current iteration
//        	//System.out.println(hits[rank].score); //score of current iteration
//        	String twttime = hitDoc.get("Date"); // get tweet's date
//            String[] splittwt = twttime.split("\\s+"); // turn string to array
//            String split2twt = splittwt[2] + ' ' + splittwt[1] + ' ' + splittwt[5];
//    		Date currdate2twt=format1.parse(split2twt);
//            String currdate3twt = format2.format(currdate2twt);
//            
//            splittwt = currdate3twt.split("\\s+");
//            int d2 = Integer.parseInt(splittwt[0]); //date
//            int m2 = Integer.parseInt(splittwt[1]); // month
//            int y2 = Integer.parseInt(splittwt[2]); // year
//        	
//        	//calculate Julian day distance
//        	int dist = diff(y1,m1,d1,y2,m2,d2);
//        	int scoremult = 0; //int to multiply our relevance score
//        	if (dist <= 1) { //less than 1 day
//        		scoremult = 5;
//        	} else if (dist <= 3) { // less than 3 day
//        		scoremult = 4;
//        	} else if (dist <= 7) { // less than 7 day
//        		scoremult = 3;
//        	} else if (dist <= 30) { // less than 30 day
//        		scoremult = 2;
//        	} else { //after 30 day
//        		scoremult = 1; 
//        	}
//        	      	
//        	//System.out.println(hits[rank].score); //score of current iteration
//        	hits[rank].score = hits[rank].score*scoremult;
//        	//System.out.println(scoremult); //score of multiplier
//        	//System.out.println(hits[rank].score); //score of current iteration after multiplier
//        }    
//
//        Pair[] arr = new Pair[hits.length];
//        for (int k = 0; k < hits.length; ++k) {
//        	arr[k] = new Pair (k, hits[k].score);
//        }
//        Arrays.sort(arr);
//        
//        for (int rank = 0; rank < hits.length; ++rank) {
//        	int rerank = arr[rank].index;
//            Document hitDoc = indexSearcher.doc(hits[rerank].doc);
//            System.out.println((rank + 1) + " (score:" + hits[rerank].score + ") " + hitDoc.get("Date") + " --> " +
//                               hitDoc.get("Name") + " - " + hitDoc.get("Text"));
//           // System.out.println(indexSearcher.explain(query, hits[rank].doc));
//        }
//
//        indexReader.close();
//        directory.close();
	}
	
}