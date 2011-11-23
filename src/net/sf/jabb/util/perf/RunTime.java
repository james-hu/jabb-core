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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
	protected String description;
	protected List<RunTime> detail;
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
		this.description = description;
		statistics = new BasicNumberStatistics();
		firstRunStartTime = new AtomicLong(0);
		detail = Collections.synchronizedList(new LinkedList<RunTime>());
	}
	
	/**
	 * Add one record of detail.<br>
	 * ����һ����ϸ��¼��
	 * 
	 * @param description	Any text that describes the detailed RunTime.
	 * @return	The detailed RunTime created.
	 */
	public RunTime addDetail(String description){
		RunTime child = new RunTime(description);
		detail.add(child);
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
	 * @return
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
		sb.append(attachment);
		sb.append('\n');
		
		for (int i = 0; i < detail.size(); i ++){
			sb.append("    ");
			sb.append(detail.get(i).toString(false));
		}
		
		return sb.toString();
	}
	
	/**
	 * Get run time which is the add-up of the run duration of all threads.<br>
	 * ���ʵ�ʵ�����ʱ�䣬���������̵߳�ִ��ʱ��֮�͡�
	 * 
	 * @return
	 */
	public long getTotalRunTime() {
		if (detail.size() > 0){
			long runTime = 0;
			for (int i = 0; i < detail.size(); i ++){
				runTime += detail.get(i).getTotalRunTime();
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
	public List<RunTime> getDetail() {
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
