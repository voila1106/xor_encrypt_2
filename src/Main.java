import java.io.*;
import java.security.*;

public class Main
{
	static byte[] cont;
	static int key;

	public static void main(String[] args) throws Exception
	{
		if(args.length < 3 || !args[2].equals("enc") && !args[2].equals("dec"))
		{
			System.out.println("Usage: exec filename key method(enc/dec)");
			System.out.println("\tExample: exec E:\\p.txt 1234 enc");
			return;
		}
		key = key(args[1]);
		FileInputStream fi=new FileInputStream(args[0]);
		cont=fi.readAllBytes();
		fi.close();
		if(cont.length==0)
		{
			System.out.println("File is empty");
			return;
		}

		switch(args[2])
		{
			case "enc" -> enc();
			case "dec" -> dec();
		}
		FileOutputStream fo=new FileOutputStream(args[0]);
		fo.write(cont);
		fo.close();

		System.out.println("Done");
	}

	static void enc()
	{
		cont[0]^=key;
		for(int i = 1; i < cont.length; i++)
		{
			String pref=new String(cont,0,i);
			int pkey=key(pref);
//			System.out.println("pref: "+pref);
//			System.out.println("pkey: "+pkey);
//			System.out.println();
			cont[i]^=(pkey^key);
		}
	}

	static void dec()
	{
		if(cont.length==1)
		{
			cont[0]^=key;
			return;
		}
		for(int i = cont.length-1; i > 0; i--)
		{
			//cont[i]^=(key(new String(cont,0,i))^key);

			String pref=new String(cont,0,i);
			int pkey=key(pref);
//			System.out.println("pref: "+pref);
//			System.out.println("pkey: "+pkey);
//			System.out.println();
			cont[i]^=(pkey^key);

		}
		cont[0]^=key;
	}

	static int key(String str)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] b = md.digest(str.getBytes());
			StringBuilder sb = new StringBuilder();
			for(byte t : b)
			{
				int u = t;
				if(u < 0)
				{
					u -= 0xffffff00;
				}
				sb.append(Integer.toHexString(u));
			}
			long l = Long.parseLong(sb.substring(0, 8), 16);
			if(l > 0x7fffffffL)
				l /= 2;
			return (int)l;
		}catch(Exception e)
		{
			System.err.println(e.toString());

			System.exit(1);
			return 1;
		}
	}

}
