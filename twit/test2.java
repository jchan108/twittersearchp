package twit;

import twitter4j.*;
import java.io.*;
import twitter4j.conf.ConfigurationBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.net.MalformedURLException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;




public class test2 implements Runnable 
{
	

	private String JSONLocation;
	public test2(String JSONLocation)
	{
		this.JSONLocation = JSONLocation;
	}
	

	@SuppressWarnings("unchecked")
	public void run() 
	{
		JSONParser parser = new JSONParser();
		try
		{
			String filePath = JSONLocation;
			System.out.println("Reading " + filePath);
			ArrayList<String> tweetlist = new ArrayList<String>();
			org.json.simple.JSONArray a = (org.json.simple.JSONArray) parser.parse(new FileReader(filePath));
			for(Object o : a)
			{
				JSONObject tweet = (JSONObject) o;
				String urlList = (String) tweet.get("url title");
				ArrayList<String> titles = new ArrayList<String>();
				if (urlList.length() > 2)
				{
					String array[] = urlList.split("\"");
					for(int i = 0; i < array.length; i++)
					{
						if (!array[i].equals( "[") && !array[i].equals( "]") && !array[i].equals(","))
							try 
							{
								URL url = new URL (array[i]);
								if (url.getHost() != null)
								{
									Document doc;
									try
									{
										doc = Jsoup.connect(array[i]).get();
										titles.add(doc.title());
										
									}
									catch (Exception e) 
									{/*System.err.println(" Failed to Fetch title");*/}
								}
							}
							catch (MalformedURLException e)
							{}
					}
					
				}
				tweet.put("titles", titles);
				tweetlist.add(tweet.toString());
			}
			try {
				String filename = filePath.substring(0, 10) + "Y" + filePath.substring(11);
				System.out.println("Writing to " + filename);
        		File file = new File(filename);
				FileWriter fw = new FileWriter(file);
				fw.write("[");
				fw.flush();
				for(int i = 0; i < tweetlist.size(); i++) {
					fw.write(tweetlist.get(i));
					if (i != tweetlist.size()-1)
						fw.write(",");
					fw.flush();
				}
				fw.write("]");
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
    	
	}
	
	public static void main(String[] args) throws TwitterException, IOException{
		{

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
				.setOAuthConsumerKey("PpAmXRWYREfx0RW7hDTZmG7Mn")
				.setOAuthConsumerSecret("thS7IICHAGcaKdjsfM32slX66WFD1LNC2vvk6y9o6QfRZSU582")
				.setOAuthAccessToken("805495253469757440-MGSTzgeWhUFOZKQIBPX9o0qPPh5elnf")
				.setOAuthAccessTokenSecret("otIvyioCf58H7PKbpgj8tfjROfVKUeQQFSqP3eB44OMkE");
			
			StatusListener listener = new StatusListener() 
			{
				int lines = 0;
				int filecount = 0;
				ArrayList<String> tweetlist = new ArrayList<String>();
	            @SuppressWarnings("unchecked")
				public void onStatus(Status status) 
	            {
	            	if(status.getLang().contains("en")) {
	                	lines++;
	                	
		            	JSONArray users, hashtags, titles;
		            	JSONObject tweet = new JSONObject();
		            	boolean hasGeo = false;
		            	

		            	String text = status.getText();
		            	
		            	//Fetches all User mentions within the tweet and prints them
		            	UserMentionEntity[] userList = status.getUserMentionEntities();
		            	users = new JSONArray();
		            	for(int i = 0; i < userList.length; i++) {
//		            		System.out.println("	User " + (i+1) + ": " + userList[i].getText());
		            		users.put(userList[i].getText());
		            	}
		            	
		            	
		            	//Fetches all URLs within the tweet and prints them
		            	titles = new JSONArray();
		            	URLEntity[] urlList = status.getURLEntities();
		            	for(int i = 0; i < urlList.length; i++) {
//		            		System.out.println("	URL " + (i+1) + ": " + urlList[i].getExpandedURL());
		            		titles.put(urlList[i].getExpandedURL());
//		            		System.out.println("   " + titles.toString() );
		            	}
		            	
		            	//Fetches all Hashtags within the tweet and prints them
		            	HashtagEntity[] hashtagList = status.getHashtagEntities();
		            	hashtags = new JSONArray();
		            	for(int i = 0; i < hashtagList.length; i++) {
//		            		System.out.println("	Hashtag " + (i+1) + ": " + hashtagList[i].getText());
		            		hashtags.put(hashtagList[i].getText());
		            	}
		            	
		            	//Fetches all media within a tweet (video, gif, picture etc.) and prints them (We won't need this unless we really want to add more features)
		            	MediaEntity[] mediaList = status.getMediaEntities();
		            	for(int i = 0; i < mediaList.length; i++) {
//		            		System.out.println("	Media " + (i+1) + " type : " + mediaList[i].getType() + " - " + mediaList[i].getExpandedURL());
		            	}
		            	
		            	if(status.getGeoLocation() != null) {
		            		hasGeo = true;
//		            		System.out.println(status.getPlace().getFullName() + " " + status.getPlace().getCountry());
		            	}
		            	
		            	//twoot = new Tweet(status);
		            	
		            	

		            	tweet.put("name", status.getUser().getName());
						tweet.put("username", status.getUser().getScreenName());
						tweet.put("mentions", users);
						tweet.put("hashtags", hashtags);
//							tweet.put("urls", urlList);
						tweet.put("url title", titles.toString());
						tweet.put("text", text);
						tweet.put("date", status.getCreatedAt().toString());
						
						if(hasGeo == true) {
							tweet.put("state", status.getPlace().getFullName());
							tweet.put("country", status.getPlace().getCountry());
							tweet.put("lat", status.getGeoLocation().getLatitude());
							tweet.put("long", status.getGeoLocation().getLongitude());
						}
//		            	System.out.print(lines);
//		            	System.out.println(tweet.toString());
		            	
		            	int filesize = 1000;
		            	if (lines%filesize == 1)
		            		tweetlist.add("[" +tweet.toString() + ",\n");
		            	else if (lines%filesize ==0)
		            		tweetlist.add( tweet.toString() + "\n");
		            	else
		            		tweetlist.add(tweet.toString() + ",\n");
		            	
		            	if(lines%filesize == 0) {
		        			String filename = "tweets/num1" + filecount + ".json";
		                	try {
		                		File file = new File(filename);
								FileWriter fw = new FileWriter(file);
								for(int i = 0; i < tweetlist.size(); i++) {
									fw.write(tweetlist.get(i));
									fw.flush();
								}
								fw.write("]");
								fw.flush();
								fw.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
		                	tweetlist.clear();
		                	filecount++;
		                	(new Thread(new test2(filename))).start();
		            	}
		            	
	            	}
	            	
	            }//end

	            @Override
	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	            }

	            @Override
	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	            }

	            @Override
	            public void onScrubGeo(long userId, long upToStatusId) {
//	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	            }

	            @Override
	            public void onStallWarning(StallWarning warning) {
//	                System.out.println("Got stall warning:" + warning);
	            }

	            @Override
	            public void onException(Exception ex) {
	                ex.printStackTrace();
	            }
	        };
			    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
			    twitterStream.addListener(listener);
			    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
			    twitterStream.sample();
			}
		}
	}