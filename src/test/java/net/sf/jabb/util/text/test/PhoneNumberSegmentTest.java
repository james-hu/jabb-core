package net.sf.jabb.util.text.test;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jabb.util.text.DurationFormatter;
import net.sf.jabb.util.text.StringStartWithMatcher;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PhoneNumberSegmentTest {
	static final String dataFile = "LOG20100613_MOBILE.txt";
	static final int MAX_DATA_SIZE = 1000;
	static List<PhoneNumberSegment> segments;
	static StringStartWithMatcher matcher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		segments = new LinkedList<PhoneNumberSegment>();
		System.out.println("Start reading data file...");
		InputStream is = PhoneNumberSegmentTest.class.getResourceAsStream(dataFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		int i = 0;
		while ((line = br.readLine()) != null && i++ < MAX_DATA_SIZE){
			String[] ss = line.split("~");
			PhoneNumberSegment s = new PhoneNumberSegment();
			s.startNumber = ss[0];
			s.endNumber = ss[1];
			s.city = ss[2];
			s.province = ss[3];
			s.dddPrefix = ss[4];
			segments.add(s);
		}
		System.out.println("Phone number segments read in: " + segments.size());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void moreMemory(){
		System.out.println("***** use more memory ****");
		matcher = phoneNumberSegmentMatching(true);
		System.out.println("**************************");
	}
	
	//@Test
	public void lessMemory(){
		System.out.println("***** use less memory ****");
		matcher = phoneNumberSegmentMatching(false);
		System.out.println("**************************");
	}
	
	//@Test
	public void byClone() throws IOException, ClassNotFoundException{
		System.out.println("***** by clone ****");
		long t1 = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(matcher);
		byte[] binary = baos.toByteArray();
		
		long t2 = System.currentTimeMillis();
		System.out.println("size=" + binary.length + "  serialized in " + DurationFormatter.format(t2-t1));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(binary);
		ObjectInputStream ois = new ObjectInputStream(bais);
		StringStartWithMatcher m2 = (StringStartWithMatcher) ois.readObject();
		System.out.println("deserialized in " + DurationFormatter.formatSince(t2));
		
		PhoneNumberSegment s = (PhoneNumberSegment) m2.match("13817726996");
		System.out.println(s == null ?  "null" : s.city);

	}
	
	public StringStartWithMatcher phoneNumberSegmentMatching(boolean useMoreMemory){
		Map<String,PhoneNumberSegment> headers = new HashMap<String,PhoneNumberSegment>();
		for (PhoneNumberSegment s: segments){
			StringStartWithMatcher.expandNumberMatchingRange(headers, s.startNumber, s.endNumber, s);
		}
		System.out.println("Phone number segment headers: " + headers.size());
		long t1 = System.currentTimeMillis();
		StringStartWithMatcher m = new StringStartWithMatcher(headers, useMoreMemory);
		System.out.println("Time used for initializing StringStartWithMatcher: " + DurationFormatter.formatSince(t1));
		
		PhoneNumberSegment s = (PhoneNumberSegment) m.match("13817726996");
		System.out.println(s == null ?  "null" : s.city);
		
		long t2 = System.currentTimeMillis();
		for (int i = 0; i < 1000; i ++){
			m.match("13817726996");
			m.match("13012345678");
			m.match("13817726346");
			m.match("13531234578");
			m.match("13747726996");
			m.match("13912345678");
			m.match("13317726996");
			m.match("13014445678");
			m.match("13817666996");
			m.match("13955345678");
		}
		System.out.println("Time used for matching x 10K: " + DurationFormatter.formatSince(t2));
		return m;
	}

}

class PhoneNumberSegment implements Serializable{
	private static final long serialVersionUID = -2460626856090916673L;
	String startNumber;
	String endNumber;
	String province;
	String city;
	String dddPrefix;
}
