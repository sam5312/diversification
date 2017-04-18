package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * this class is basically written to process nbn and flu tweets
 * to remove non english tweets
 * 
 * This class is also used to remove stopwords and to clean tweets and to save them back
 * @author sam
 *
 */

public class TweetParser
{

	//10jan16; word2vec
//	static String inputDir =  "/media/fatcat/sameendra/personalizedsearch/exp_dec24/users/";
//	static String outputDir =  "/media/fatcat/sameendra/personalizedsearch/exp_dec24/users_processed/";	

//	static String inputDir =  "/media/sam/Sam/twitter_personalization/lists/";
//	static String outputDir =  "/media/sam/Sam/twitter_personalization/lists_preprssed/swr/";
	
	static String inputDir = "/media/sam/Sam/twitter_personalization/w2v_tests/biggerdataset_twitter_19May/eu_text";
//	static String outputDir = "/media/sam/Sam/twitter_personalization/w2v_tests/biggerdataset_twitter_19May/eu_text_pp/";
	
	static String outputDir = "/media/fatcat/sameendra/stanford-tweets-ds/tm3/tm3_pp/";

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main( String[] args ) throws Exception
	{

		System.out.println("started input: " + inputDir );	
		
	
//		processDir( new File(inputDir).listFiles() );
		processtm3( new File("/media/fatcat/sameendra/stanford-tweets-ds/tm3/tweets2009-09.txt") );
		
		System.out.println("done");
	}
	
	private static void processtm3(File file) throws Exception
	{

		System.out.println("processing file " + file.getName());
		BufferedReader br = new BufferedReader( new FileReader( file ) );

		String line;
		while ( ( line = br.readLine() ) != null ) // for each tweet
		{
		//	parseTweet( line, dataDir + file.getName(),true );
			parseTweet( line, outputDir + file.getName(), false);
		}					
		br.close();
	}
	
	private static void processDir( File[] files ) throws IOException
	{
		for ( File file : files )
		{
			if ( file.isDirectory() )
			{
				//do nothing	
			}
			else //if it's a file
			{
				//if ( file.getName().length() == 13 ) //only for 'nbn_xx_xx.dat' files
				{
					System.out.println("processing file " + file.getName());
					BufferedReader br = new BufferedReader( new FileReader( file ) );

					String line;
					while ( ( line = br.readLine() ) != null ) // for each tweet
					{
					//	parseTweet( line, dataDir + file.getName(),true );
						parseTweet( line, outputDir + file.getName(), false);
					}					
					br.close();
				}
			}
		}
	}

	/**
	 * 
	 * @param tweet
	 * @param newfilename
	 * @param isTweetJson pass true when JSON object is passed. Pass false if a string is passed
	 */
	private static void parseTweet(String tweet, String newfilename, boolean isTweetJson)
	{		
		StringBuilder tweetTxtStrBuilder = new StringBuilder();
		try
		{
			String tweetTxt = null;
			
			if (isTweetJson) //tweeet json is passed
			{
				JsonParser parser = new JsonParser();
				JsonObject jsonObj = ( JsonObject ) parser.parse( tweet );
				JsonElement jsonTxtEle = jsonObj.get( "text" );
				JsonElement idEle = jsonObj.get( "id_str" );
				JsonElement langEle = null;
				tweetTxt = jsonTxtEle.getAsString();
			}
			else //a string is passed
			{
				tweetTxt = tweet;
			}
			
			
			tweetTxtStrBuilder = new StringBuilder( tweetTxt ); //tweet text
			
			try
			{
				{

					if (tweetTxtStrBuilder.toString().trim().length() > 0)
					{
						String cleanedTweet = TweetProcessingUtil.processTweetText( tweetTxtStrBuilder.toString(), "^[^#@$0-9a-zA-Z]+","[^0-9a-zA-Z]+$", 1 ); //keeps the $ sign as well
						
						if ( cleanedTweet.trim().length() > 0) //only if its a valid tweet
						{
							//writeToFile( new File(newfilename), (idEle.getAsString()+ " " + cleanedTweet) );
							FileUtil.writeToFile( new File(newfilename), cleanedTweet );
						}
					}
				}
	
			}
			catch ( NullPointerException e )
			{
				System.out.println(e);
				System.exit( 1 );
			}
		
		}
		catch ( Exception e ) // expanded_url can be null
		{
			System.out.println( e  + " "  + tweet);
			System.exit( 1 );
		}
	}
	
	
	
	
}
