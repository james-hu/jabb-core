/*
Copyright 2011 Zhengmao HU (James)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.sf.jabb.util.perf;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jabb.util.col.PutOnGetMap;
import net.sf.jabb.util.stat.BasicNumberStatistics;
import net.sf.jabb.util.text.DurationFormatter;

/**
 * Record of time spent on something.<br>
 * ��ĳ�������ʱ��ļ�¼��
 * <p>
 * Duration is defined to be the period of time from the beginning to the end of an action.
 * Run Time is defined to be the accumulated duration spent in all working threads.
 * If there is no multi-threading, then Run Time should be the same as Duration.
 * <p>
 * Duration�Ķ�����ĳ���ӿ�ʼ��������ʱ��Ρ�
 * Run Time�Ķ��������й����̵߳�Duration֮�͡�
 * ���û�ж��̴߳�����Run Time��Duration��һ���ġ�
 * <p>
 * start() and end() must be invoked in pair in same thread. 
 * But pairs of start() and end() can be invoked in multi-thread environment.
 * add() is equivalent to a pair of start() and end().
 * <p>
 * start()��end()������ͬһ���߳��гɶԵ��á�
 * ���Ǹ���start()��end()�����ڶ��߳������ʹ�á�
 * add()������������һ��start()��end()��
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RunTime {
	protected static final String INDENT = "  ";
	
	protected String description;
	protected PutOnGetMap<String, RunTime> detail;
	protected Object attachment;
	
	protected BasicNumberStatistics statistics;
	
	protected AtomicLong firstRunStartTime;
	protected long lastRunStartTime;
	
	/**
	 * Constructor.<br>
	 * ���췽����
	 * 
	 * @param description	Any text that describes this RunTime.
	 */
	public RunTime(String description){
		this();
		this.description = description;
	}
	
	/**
	 * Construct an instance without description text.
	 * ����һ��descriptionΪ�յ�ʵ����
	 */
	public RunTime(){
		statistics = new BasicNumberStatistics();
		firstRunStartTime = new AtomicLong(0);
		detail = new PutOnGetMap<String, RunTime>(new LinkedHashMap<String, RunTime>(), RunTime.class);
	}
	
	/**
	 * Reset to initial status.<br>
	 * �ظ�����ʼ״̬��
	 */
	public void reset(){
		statistics.reset();
		firstRunStartTime.set(0);
		lastRunStartTime = 0;
		detail.clear();
	}
	
	/**
	 * Create a detail record and add it as a child.<br>
	 * ����һ����ϸ��¼����Ϊ�¼���
	 * 
	 * @param description	Any text that describes the detailed RunTime.
	 * @return	The detailed RunTime created.
	 */
	public RunTime addDetail(String description){
		RunTime child = new RunTime(description);
		detail.put(description, child);
		return child;
	}
	
	/**
	 * Add an existing detail record as a child.<br>
	 * ��һ�����е���ϸ��¼��Ϊ�¼���
	 * 
	 * @param child 	an existing record that need to be added as a child.
	 */
	public void addDetail (RunTime child){
		detail.put(child.description, child);
	}
	
	/**
	 * Get specified detail record, if it does not exist yet, create it first.<br>
	 * ���ָ������ϸ��¼��������������ȴ���һ����
	 * 
	 * @param description  description of the detail record.<br>
	 * 						������ϸ��¼��description��
	 * @return  The detail record with specified description.<br>
	 * 			����ָ��description����ϸ��¼��
	 */
	public RunTime getDetail(String description){
		RunTime child = detail.get(description);
		if (child.getDescription() == null){
			child.setDescription(description);
		}
		return child;
	}
	
	/**
	 * Starts the calculation of run time. <br>
	 * ��ʼ��ʱ���������������ʱ��ͬһ���߳��б����á�
	 * <p>
	 * The pair of start() and end() must be called from the same thread.
	 */
	public void start(){
		firstRunStartTime.compareAndSet(0, System.currentTimeMillis());
		lastRunStartTime = System.nanoTime();
	}
	
	/**
	 * Starts the calculation of run time of a detail record.<br>
	 * ��ʼ��һ����ϸ��¼�ļ�ʱ���������������ʱ��ͬһ���߳��б����á�
	 * @param desc	Description of the detail record.<br>
	 * 				��ϸ��¼��Description
	 */
	public void startDetail(String desc){
		this.getDetail(desc).start();
	}
	
	/**
	 * Ends the calculation of run time of a detail record.<br>
	 * ������һ����ϸ��¼�ļ�ʱ���������뿪ʼ��ʱ��ͬһ���߳��б����á�
	 * @param desc	Description of the detail record.<br>
	 * 				��ϸ��¼��Description
	 */
	public void endDetail(String desc){
		this.getDetail(desc).end();
	}
	
	/**
	 * Ends the calculation of run time.<br>
	 * ������ʱ���������뿪ʼ��ʱ��ͬһ���߳��б����á�
	 * <p>
	 * The pair of start() and end() must be called from the same thread.
	 */
	public void end(){
		statistics.put(System.nanoTime() - lastRunStartTime);
	}
	
	/**
	 * Add one run time.<br>
	 * ����һ��RunTime��
	 * 
	 * @param milliStartTime	Start time in milliseconds (usually from System.currentTimeMillis())
	 * @param nanoDurationTime	Run time duration in nanoseconds.
	 */
	public void add(long milliStartTime, long nanoDurationTime){
		firstRunStartTime.compareAndSet(0, milliStartTime);
		statistics.put(nanoDurationTime);
	}
	
	/**
	 * Add one run time to a specified detail record.<br>
	 * ��ָ������ϸ��¼����һ��RunTime��
	 * 
	 * @param desc	Description of the detail record.<br>
	 * 				��ϸ��¼��Description
	 * @param milliStartTime	Start time in milliseconds (usually from System.currentTimeMillis())
	 * @param nanoDurationTime	Run time duration in nanoseconds.
	 */
	public void addDetail(String desc, long milliStartTime, long nanoDurationTime){
		this.getDetail(desc).add(milliStartTime, nanoDurationTime);
	}

	/**
	 * Output to a TAB separated text which can be pasted into Excel.<br>
	 * ����ɿ�������Excel����TAB�ָ����ı���
	 * <p>
	 * The first row is the column headers. Fields in each following line:<br>
	 * ��һ�����б��⡣����ÿ�е��ֶ����£�<p>
	 * <code>
	 * Description with left indent according to hierarchical structure
	 * First Run Start Time (in text format, in milliseconds)
	 * Run Count
	 * Total Duration (in text format, in milliseconds)
	 * Total Duration (in nanoseconds)
	 * Total Run Time (in text format, in milliseconds)
	 * Total Run Time (in nanoseconds)
	 * Average Duration (in text format, in milliseconds)
	 * Average Duration (in nanoseconds)
	 * Minimal Duration (in text format, in milliseconds)
	 * Minimal Duration (in nanoseconds)
	 * Maximal Duration (in text format, in milliseconds)
	 * Maximal Duration (in nanoseconds)
	 * Attachment
	 * </code>
	 */
	@Override
	public String toString(){
		return toString(true);
	}
	
	/**
	 * 
	 * @param header	true - with header row; false - without
	 * @return the string represents this object
	 */
	public String toString(boolean header){
		StringBuilder sb = new StringBuilder();
		long totalRunTime = getTotalRunTime();
		
		if (header){
			sb.append("Description").append('\t')
			.append("First Run").append('\t')
			.append("Run Count").append('\t')
			.append("Total Duration").append('\t')
			.append("Total Duration Nano").append('\t')
			.append("Total RunTime").append('\t')
			.append("Total RunTime Nano").append('\t')
			.append("Avg RunTime").append('\t')
			.append("Avg RunTime Nano").append('\t')
			.append("Min RunTime").append('\t')
			.append("Min RunTime Nano").append('\t')
			.append("Max RunTime").append('\t')
			.append("Max RunTime Nano").append('\t')
			.append("Attachment").append('\n');
		}
		sb.append(description).append('\t');
		if (statistics.getCount() > 0){
			sb.append(new Date(getFirstRunStartTime())).append('\t');
			sb.append(String.format("%,d", statistics.getCount())).append('\t');
			sb.append(' ').append(DurationFormatter.format(statistics.getSum()/1000000)).append('\t');
			sb.append(String.format("%,d", statistics.getSum())).append('\t');
			sb.append(' ').append(DurationFormatter.format(totalRunTime/1000000)).append('\t');
			sb.append(String.format("%,d", totalRunTime)).append('\t');
			sb.append(' ').append(DurationFormatter.format((long)statistics.getAvg()/1000000)).append('\t');
			sb.append(String.format("%,d", (long)statistics.getAvg())).append('\t');
			sb.append(' ').append(DurationFormatter.format((long)statistics.getMin()/1000000)).append('\t');
			sb.append(String.format("%,d", (long)statistics.getMin())).append('\t');
			sb.append(' ').append(DurationFormatter.format((long)statistics.getMax()/1000000)).append('\t');
			sb.append(String.format("%,d", (long)statistics.getMax())).append('\t');
		} else {
			sb.append("\t\t\t\t\t\t\t\t\t\t\t\t");
		}
		sb.append(attachment);
		sb.append('\n');
		
		for (RunTime child: detail.values()){
			sb.append(INDENT);
			sb.append(child.toString(false).replace("\n", "\n" + INDENT));
			sb.setLength(sb.length() - INDENT.length());
		}
		
		return sb.toString();
	}
	
	/**
	 * Get run time which is the add-up of the run duration of all threads.<br>
	 * ���ʵ�ʵ�����ʱ�䣬���������̵߳�ִ��ʱ��֮�͡�
	 * 
	 * @return add-up of the run duration of all threads
	 */
	public long getTotalRunTime() {
		if (detail.size() > 0){
			long runTime = 0;
			for (RunTime child: detail.values()){
				runTime += child.getTotalRunTime();
			}
			return runTime;
		}else{
			return statistics.getSum();
		}
	}
	
	public long getFirstRunStartTime() {
		return firstRunStartTime.get();
	}
	
	/**
	 * Get the duration.<b>
	 * ��������ڼ������ʱ��Ρ�
	 * 
	 * @return	in nanoseconds
	 */
	public long getTotalDuration() {
		return statistics.getSum();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, RunTime> getAllDetail() {
		return detail;
	}
	public Object getAttachment() {
		return attachment;
	}
	/**
	 * Set an object to be attached to this RunTime object.
	 * @param attachment	can be anything.
	 */
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}
	
	

}
