/*
Copyright 2011-2012 Zhengmao HU (James)

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

import net.sf.jabb.util.col.PutIfAbsentMap;
import net.sf.jabb.util.stat.BasicNumberStatistics;
import net.sf.jabb.util.text.DurationFormatter;

/**
 * Record of time spent on something.<br>
 * 对某项工作所花时间的记录。
 * <p>
 * Duration is defined to be the period of time from the beginning to the end of an action.
 * Run Time is defined to be the accumulated duration spent in all working threads.
 * If there is no multi-threading, then Run Time should be the same as Duration.
 * <p>
 * Duration的定义是某项活动从开始到结束的时间段。
 * Run Time的定义是所有工作线程的Duration之和。
 * 如果没有多线程处理，则Run Time与Duration是一样的。
 * <p>
 * start() and end() must be invoked in pair in same thread. 
 * But pairs of start() and end() can be invoked in multi-thread environment.
 * add() is equivalent to a pair of start() and end().
 * <p>
 * start()和end()必须在同一个线程中成对调用。
 * 但是各对start()和end()可以在多线程情况下使用。
 * add()可以用来代替一对start()和end()。
 * 
 * @author Zhengmao HU (James)
 *
 */
public class RunTime {
	protected static final String INDENT = "  ";
	
	protected String description;
	protected PutIfAbsentMap<String, RunTime> detail;
	protected Object attachment;
	
	protected BasicNumberStatistics statistics;
	
	protected AtomicLong firstRunStartTime;
	protected long lastRunStartTime;
	
	/**
	 * Constructor.<br>
	 * 构造方法。
	 * 
	 * @param description	Any text that describes this RunTime.
	 */
	public RunTime(String description){
		this();
		this.description = description;
	}
	
	/**
	 * Construct an instance without description text.
	 * 创建一个description为空的实例。
	 */
	public RunTime(){
		statistics = new BasicNumberStatistics();
		firstRunStartTime = new AtomicLong(0);
		detail = new PutIfAbsentMap<String, RunTime>(new LinkedHashMap<String, RunTime>(), RunTime.class);
	}
	
	/**
	 * Reset to initial status.<br>
	 * 回复到初始状态。
	 */
	public void reset(){
		statistics.reset();
		firstRunStartTime.set(0);
		lastRunStartTime = 0;
		detail.clear();
	}
	
	/**
	 * Create a detail record and add it as a child.<br>
	 * 创建一条详细记录并加为下级。
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
	 * 将一条已有的详细记录加为下级。
	 * 
	 * @param child 	an existing record that need to be added as a child.
	 */
	public void addDetail (RunTime child){
		detail.put(child.description, child);
	}
	
	/**
	 * Get specified detail record, if it does not exist yet, create it first.<br>
	 * 获得指定的详细记录，如果不存在则先创建一个。
	 * 
	 * @param description  description of the detail record.<br>
	 * 						这条详细记录的description。
	 * @return  The detail record with specified description.<br>
	 * 			具有指定description的详细记录。
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
	 * 开始计时，它必须与结束计时在同一个线程中被调用。
	 * <p>
	 * The pair of start() and end() must be called from the same thread.
	 */
	public void start(){
		firstRunStartTime.compareAndSet(0, System.currentTimeMillis());
		lastRunStartTime = System.nanoTime();
	}
	
	/**
	 * Starts the calculation of run time of a detail record.<br>
	 * 开始对一个详细记录的计时，它必须与结束计时在同一个线程中被调用。
	 * @param desc	Description of the detail record.<br>
	 * 				详细记录的Description
	 */
	public void startDetail(String desc){
		this.getDetail(desc).start();
	}
	
	/**
	 * Ends the calculation of run time of a detail record.<br>
	 * 结束对一个详细记录的计时，它必须与开始计时在同一个线程中被调用。
	 * @param desc	Description of the detail record.<br>
	 * 				详细记录的Description
	 */
	public void endDetail(String desc){
		this.getDetail(desc).end();
	}
	
	/**
	 * Ends the calculation of run time.<br>
	 * 结束计时，它必须与开始计时在同一个线程中被调用。
	 * <p>
	 * The pair of start() and end() must be called from the same thread.
	 */
	public void end(){
		statistics.put(System.nanoTime() - lastRunStartTime);
	}
	
	/**
	 * Add one run time.<br>
	 * 增加一次RunTime。
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
	 * 给指定的详细记录增加一次RunTime。
	 * 
	 * @param desc	Description of the detail record.<br>
	 * 				详细记录的Description
	 * @param milliStartTime	Start time in milliseconds (usually from System.currentTimeMillis())
	 * @param nanoDurationTime	Run time duration in nanoseconds.
	 */
	public void addDetail(String desc, long milliStartTime, long nanoDurationTime){
		this.getDetail(desc).add(milliStartTime, nanoDurationTime);
	}

	/**
	 * Output to a TAB separated text which can be pasted into Excel.<br>
	 * 输出成可以贴进Excel的由TAB分隔的文本。
	 * <p>
	 * The first row is the column headers. Fields in each following line:<br>
	 * 第一行是列标题。后续每行的字段如下：<p>
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
	 * 获得实际的运行时间，它是所有线程的执行时间之和。
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
	 * 获得运行期间所跨的时间段。
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
