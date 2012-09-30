package sachjo.myapp2encodingtest;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		int[] inputStrNameArray = new int[] {
			R.string.inputstring,
			R.string.string_halfkana,
			R.string.string_ibmspe,
			R.string.necibmstring0,
			R.string.necibmstring1,
			R.string.necibmstring2,
			R.string.necibmstring3,
			R.string.necibmstring4,
		};
		String[] inputStrArray = new String[inputStrNameArray.length];
		byte[][] eucjpstrArray = new byte[inputStrNameArray.length][];
		//String[] charsetNameArray = new String[] { "x-eucJP-OpenExt", "EUC-JP" , "EUC-JPwin", "EUC-JP-Open2","EUC-JP-Open3"};
		String[] charsetNameArray = new String[] { "EUC-JPwin","EUC-JP-Open3"};
		String outStr = "Original:\n";
		for (int i = 0; i < inputStrNameArray.length; i++)
		{
			String str = getResources().getString(inputStrNameArray[i]);
			str = str.replace("\t","");
			str = str.replace(" ","");
			outStr += str + "\n";
			inputStrArray[i] = str;
		}
		outStr += "\n\n";
		try
		{
			for (String charsetName : charsetNameArray)
			{
				outStr += "utf->" + charsetName + ":\n";
				//for (String inputStr : inputStrArray)
				for(int i =0;i < inputStrNameArray.length;i++)
				{
					eucjpstrArray[i] = inputStrArray[i].getBytes(charsetName);

					for (byte b : eucjpstrArray[i]) outStr += " " + (b < 0 ? Integer.toHexString(b).substring(6) : Integer.toHexString(b));
					outStr += "\n";
				}
				outStr += charsetName + "->utf:\n";
				//for (String inputStr : inputStrArray)
				for(int i = 0;i < inputStrNameArray.length;i++)
				{
					outStr += new String(eucjpstrArray[i], charsetName);
					outStr += "\n";
				}
				outStr += "\n\n";
			}
			//byte[] sjis = str.getBytes("SJIS"); 
			//System.out.println("SJIS :" + new String(sjis, "SJIS"));

		}
		catch (UnsupportedEncodingException e)
		{
			outStr += "UnsupportedEncodingException occured: " + e.getMessage();
		}
		catch (Exception e)
		{
			outStr += "UnhandledException occured: " + e.getMessage();
		}
		System.out.println(outStr);
		EditText editText= (EditText) findViewById(R.id.text1);
		editText.setText(outStr);
    }
}
