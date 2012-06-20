/*
Copyright 2010-2011 Zhengmao HU (James)

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

package net.sf.jabb.util.stat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jabb.util.col.PutOnGetMap;

/**
 * Ƶ�μ�����
 * 
 * @author Zhengmao HU (James)
 *
 */
public class BasicFrequencyCounter extends FrequencyCounter {
	protected PutOnGetMap<Long, AtomicLong> counters;
	protected long granularity;
	protected long purgeBefore;
	protected Object recordLock = new Object();
	

	/**
	 * ����һ��ʵ�������purgePeriod������Ϊ0����Զ�������ʷ���������
	 * @param granularity	����������
	 * @param unit			�����ȵĵ�λ
	 * @param purgePeriod	��ʷ���ݵı���ʱ�䳤��
	 * @param purgeUnit		��ʷ���ݱ���ʱ�䳤�ȵĵ�λ
	 */
	public BasicFrequencyCounter(long granularity, TimeUnit unit,
			long purgePeriod, TimeUnit purgeUnit){
		counters = new PutOnGetMap<Long, AtomicLong>(
				new ConcurrentSkipListMap<Long, AtomicLong>(), AtomicLong.class);
		this.granularity = TimeUnit.MILLISECONDS.convert(granularity, unit);
		if (purgePeriod == 0){
			this.purgeBefore = 0;
		} else {
			this.purgeBefore = TimeUnit.MILLISECONDS.convert(purgePeriod, purgeUnit);
		}
	}

	/**
	 * ����һ��������ʷ���ݽ����Զ������ʵ����
	 * @param granularity	����������
	 * @param unit			�����ȵĵ�λ
	 */
	public BasicFrequencyCounter(long granularity, TimeUnit unit){
		this(granularity, unit, 0, null);
	}
	
	/**
	 * ����һ��������ʷ���ݽ����Զ�������Ҳ����ʱ�������
	 * �����磬����һ�����ţ��Բ�ͬ���ȶ��Ÿ��ж���������ͳ�ƣ���ʵ����
	 */
	public BasicFrequencyCounter(){
		this(1, TimeUnit.MILLISECONDS, 0, null);
	}

	/**
	 * ����������Ϣ����һ��ʵ����
	 * @param definition	������Ϣ
	 */
	public BasicFrequencyCounter(FrequencyCounterDefinition definition){
		this(definition.getGranularity(), TimeUnit.MILLISECONDS,
				definition.getPurgeBefore(), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * ��¼��ĳʱ�̷����˶��ٴΡ�
	 * @param when	������ʱ��
	 * @param times	����
	 */
	@Override
	public void count(long when, int times){
		long recWhen = when - (when % granularity);
		counters.get(recWhen).addAndGet(times);
		if (purgeBefore != 0){
			purge(when - purgeBefore);
		}
	}
	
	/**
	 * ���ȫ������ͳ��
	 * @return	���ص�Map��Key���Ժ���Ϊ��λ��ʱ�䣬value�Ǽ���ֵ��
	 */
	public Map<Long, AtomicLong> getCounts(){
		return counters.getMap();
	}
	
	/**
	 * ��ȡ��ĳʱ�̵ļ���ֵ��
	 * @param when	ʱ��
	 * @return		ͳ����
	 */
	@Override
	public long getCount(long when){
		Long recWhen = when - (when % granularity);
		AtomicLong times = counters.get(recWhen);
		return times == null ? 0 : times.longValue(); 
	}
	
	/**
	 * �����ָ��ʱ�䷶Χ�ڵ���Ƶ��
	 * @param fromWhen		��ʼʱ��
	 * @param toWhen		����ʱ��
	 * @param fromInclusive	�Ƿ������ʼʱ��
	 * @param toInclusive	�Ƿ��������ʱ��
	 * @return				ͳ����
	 */
	@Override
	public long getCount(long fromWhen, long toWhen,  boolean fromInclusive, boolean toInclusive){
		NavigableMap<Long, AtomicLong> range = counters.subMap(fromWhen, fromInclusive, toWhen, toInclusive);
		long count = 0;
		for(AtomicLong c: range.values()){
			count += c.longValue();
		}
		return count;
	}
	
	/**
	 * ɾ��������һ��ʱ��ļ�¼
	 * @param tillWhen	������ĸ�ʱ���Ϊֹ
	 */
	@Override
	public void purge(long tillWhen){
		Long t;
		while((t = counters.firstKey()) != null && t < tillWhen){
			counters.remove(t);
		}
	}
	
	/**
	 * ת��String
	 */
	public String toString(){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.format("granularity=%d(ms) purgeBefore=%d(ms)\n", granularity, purgeBefore);
		
		boolean isFirst = true;
		for (Long t: counters.keySet()){
			if (isFirst){
				isFirst = false;
			}else{
				pw.print('\n');
			}
			pw.format(" %1$tY%1$tm%1$td %1$tH:%1$tM:%1$tS.%1$tL (%1$20d) -> %2$20d", t, counters.get(t).longValue());
		}
		
		return sw.toString();
	}

}
