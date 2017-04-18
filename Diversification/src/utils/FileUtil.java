package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class FileUtil
{
	/**
	 * returns all the values from key=value pairs in the properties file.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> readPropFileAndGetValues( String file ) throws IOException  
	{
		ArrayList<String> values = new ArrayList<String>();
		
		FileInputStream fileInput = new FileInputStream( new File( file ) );
		Properties properties = new Properties();
		properties.load( fileInput );
		fileInput.close();

		Enumeration enuKeys = properties.keys();
		
		while ( enuKeys.hasMoreElements() )
		{
			String key = ( String ) enuKeys.nextElement();
			String val = properties.getProperty( key );
			values.add( val );
		}
		return values;
	}
	/**
	 * This method writes a given content string to a given file while appending
	 * @param file
	 * @param stringToWrite
	 */
	public static void writeToFile( File file, String stringToWrite) throws Exception
	{
		FileWriter fw = null;
		try
		{
			if ( !file.exists() )
			{
				if ( file.getParentFile() != null && !file.getParentFile().exists() )
				{
					boolean mkdirs = file.getParentFile().mkdirs();
					if (!mkdirs)
					{
						System.out.println( "error in creating folders" );
						System.exit( -1 );
					}
				}
				file.createNewFile();
			}
			fw = new FileWriter( file, true ); // the true will append the new data			

			fw.write( stringToWrite + "\n" );// appends the string to the file
			 
		}
		catch ( Exception ioe )
		{
			System.err.println( "Exception: " + ioe );
			throw ioe;
		}
		finally
		{
			if ( fw != null )
			{
				try
				{
					fw.close();
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * This method writes a given content string to a given file while appending
	 * @param file
	 * @param stringToWrite
	 */
	public static void writeToFile( File file, List<String> lines)
	{
		FileWriter fw = null;
		try
		{
			if ( !file.exists() )
			{
				if ( file.getParentFile() != null && !file.getParentFile().exists() )
				{
					boolean mkdirs = file.getParentFile().mkdirs();
					if (!mkdirs)
					{
						System.out.println( "error in creating folders" );
						System.exit( -1 );
					}
				}
				file.createNewFile();
			}
			fw = new FileWriter( file, true ); // the true will append the new data			

			for ( String line : lines )
			{
				fw.write( line + "\n" );// appends the string to the file
			}
			 
		}
		catch ( Exception ioe )
		{
			System.err.println( "Exception: " + ioe );
		}
		finally
		{
			if ( fw != null )
			{
				try
				{
					fw.close();
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void readLines( String file, ArrayList<String> lines )
	{
		BufferedReader reader = null;

		try
		{

			reader = new BufferedReader( new FileReader( new File( file ) ) );

			String line = null;
			while ( ( line = reader.readLine() ) != null )
			{
				lines.add( line );
			}

		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( reader != null )
			{
				try
				{
					reader.close();
				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}
			}
		}

	}
	
	public static void readLines( File file, ArrayList<String> lines )
	{
		BufferedReader reader = null;

		try
		{

			reader = new BufferedReader( new FileReader(  file  )  );

			String line = null;
			while ( ( line = reader.readLine() ) != null )
			{
				lines.add( line );
			}

		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( reader != null )
			{
				try
				{
					reader.close();
				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}
			}
		}

	}
	
	public static List<String> readLines( File file )
	{
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;

		try
		{

			reader = new BufferedReader( new FileReader(  file  )  );

			String line = null;
			while ( ( line = reader.readLine() ) != null )
			{
				lines.add( line );
			}

		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( reader != null )
			{
				try
				{
					reader.close();
				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return lines;
	}
	
	/**
	 * delete the file if it exists already
	 * @param file
	 */
	public static void delteFile(File file)
	{
		if(file.exists())
		{
			file.delete();
			System.out.println( "file " + file + " deleted sucessfully! " );
		}
	}
	
	/**
	 * used to time loggging. you have to pass the startTime as an argument
	 * @param startTime as given by System.currentTimeMillis()
	 */
	public static void printExecutionTime(long startTime)
	{
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println( "Run time = " + duration + " ms, " + (double)(duration/1000)/60 + " minutes" );

//		System.out.println( "Final Done. time taken = " + (double)(duration/1000)/60 + " minutes" );
	}

	/**
	 * returns a string give the hashset object. basically to get rid of , and [,], if you just use hashset.tostring() method.
	 * @param set
	 * @return
	 */
	public static String getStringfromHashSet(Set<String> set)
	{
		String tokens = "";
		for ( String string : set )
		{
			tokens += " " + string;
		}
		return tokens;
	}
}
