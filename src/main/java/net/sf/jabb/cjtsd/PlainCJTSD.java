/**
 * 
 */
package net.sf.jabb.cjtsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the DTO class for Compact JSON Time Series Data (CJTSD) data.
 * <p>To generate a CJTSD object, use CJTSD class in jabb-core-java8</p>
 * @see <a href="https://github.com/james-hu/cjtsd-js/wiki/Compact-JSON-Time-Series-Data">https://github.com/james-hu/cjtsd-js/wiki/Compact-JSON-Time-Series-Data</a>
 * @author James Hu (Zhengmao Hu)
 *
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
public class PlainCJTSD {
	String u;
	List<Long> t;
	List<Integer> d;
	
	List<Long> c;
	List<Number> s;
	List<Number> a;
	List<Number> m;
	List<Number> x;
	List<Number> n;
	List<Object> o;

	public PlainCJTSD(){
		
	}
	
	/**
	 * Convert into raw list form.
	 * @return	the list containing entries of data points
	 */
	public List<RawEntry> toRawList(){
		if (t == null || t.size() == 0){
			return Collections.emptyList();
		}
		
		List<RawEntry> result = new ArrayList<RawEntry>(t.size());
		int lastDuration = 0;
		for (int i = 0; i < t.size(); i ++){
			long timestamp = t.get(i);
			int duration = -1;
			if (i < d.size()){
				duration = d.get(i);
			}
			if (duration == -1){
				duration = lastDuration;
			}
			lastDuration = duration;
			
			long timestampMillis;
			long durationMillis;
			if (u == null || u.equals("m")){
				timestampMillis = 1000L * 60 * timestamp;
				durationMillis = 1000L * 60 * duration;
			}else if (u.equals("s")){
				timestampMillis = 1000L * timestamp;
				durationMillis = 1000L * duration;
			}else if (u.equals("S")){
				timestampMillis = timestamp;
				durationMillis = duration;
			}else{
				throw new IllegalArgumentException("Unit not supported: " + u);
			}
			
			result.add(new RawEntry(timestampMillis, durationMillis,
					c == null || i >= c.size() ? null : c.get(i),
					s == null || i >= s.size() ? null : s.get(i),
					a == null || i >= a.size() ? null : a.get(i),
					m == null || i >= m.size() ? null : m.get(i),
					x == null || i >= x.size() ? null : x.get(i),
					n == null || i >= n.size() ? null : n.get(i),
					o == null || i >= o.size() ? null : o.get(i)
					));
			
		}
		return result;
	}
	

	static public class RawEntry{
		long timestamp;
		long duration;
		Long count;
		Number sum;
		Number avg;
		Number min;
		Number max;
		Number number;
		Object obj;

		RawEntry(long timestamp, long duration, Long count, Number sum, Number avg, Number min, Number max, Number number, Object obj) {
			super();
			this.timestamp = timestamp;
			this.duration = duration;
			this.count = count;
			this.sum = sum;
			this.avg = avg;
			this.min = min;
			this.max = max;
			this.number = number;
			this.obj = obj;
		}

		/**
		 * Get timestamp as number of milliseconds since 00:00:00 local time, 1 January 1970
		 * @return	number of milliseconds
		 */
		public long getTimestamp() {
			return timestamp;
		}

		/**
		 * Get duration as number of milliseconds
		 * @return
		 */
		public long getDuration() {
			return duration;
		}

		public Long getCount() {
			return count;
		}

		public Number getSum() {
			return sum;
		}

		public Number getAvg() {
			return avg;
		}

		public Number getMin() {
			return min;
		}

		public Number getMax() {
			return max;
		}

		public Number getNumber() {
			return number;
		}

		public Object getObj() {
			return obj;
		}
		
		
	}

	
	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public String getU() {
		return u;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Long> getT() {
		return t;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Integer> getD() {
		return d;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Long> getC() {
		return c;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Number> getS() {
		return s;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Number> getA() {
		return a;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Number> getM() {
		return m;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Number> getX() {
		return x;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Number> getN() {
		return n;
	}

	@org.boon.json.annotations.JsonInclude(org.boon.json.annotations.JsonInclude.Include.NON_NULL)
	public List<Object> getO() {
		return o;
	}

	public void setU(String u) {
		this.u = u;
	}

	public void setT(List<Long> t) {
		this.t = t;
	}

	public void setD(List<Integer> d) {
		this.d = d;
	}

	public void setC(List<Long> c) {
		this.c = c;
	}

	public void setS(List<Number> s) {
		this.s = s;
	}

	public void setA(List<Number> a) {
		this.a = a;
	}

	public void setM(List<Number> m) {
		this.m = m;
	}

	public void setX(List<Number> x) {
		this.x = x;
	}

	public void setN(List<Number> n) {
		this.n = n;
	}

	public void setO(List<Object> o) {
		this.o = o;
	}
}
