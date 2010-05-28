/*
Copyright 2010 Zhengmao HU (James)

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

import java.util.concurrent.TimeUnit;

/**
 * Ƶ�μ�������������Ϣ
 * 
 * @author Zhengmao HU (James)
 *
 */
public class FrequencyCounterDefinition {
	protected Object id;
	protected long granularity;
	protected long purgeBefore;
	
	/**
	 * ����һ�����Զ�ɾ����ʷ���ݵ�Ƶ�μ�����������Ϣ����
	 * @param id			Ƶ�μ�������ID�����Ƶ�μ�������Ҫ����ΪPackagedFrequencyCounter��
	 * 						��һ���֣���ô���б�Ҫ����ID�����ֻ�ǵ�����ΪBasicFrequencyCounter
	 * 						ʹ�ã���ô�Ϳ�������Ϊnull��
	 * @param granularity	Ƶ��ͳ�ƵĿ����ȣ�����30��60��
	 * @param unit			�����ȵĵ�λ������TimeUnit.SECONDS��TimeUnit.MINUTES��
	 */
	public FrequencyCounterDefinition(Object id, long granularity, TimeUnit unit){
		this.id = id;
		this.granularity = TimeUnit.MILLISECONDS.convert(granularity, unit);
		this.purgeBefore = 0;
	}

	/**
	 * ����һ���Զ�ɾ����ʷ���ݵ�Ƶ�μ�����������Ϣ����
	 * @param id			Ƶ�μ�������ID�����Ƶ�μ�������Ҫ����ΪPackagedFrequencyCounter��
	 * 						��һ���֣���ô���б�Ҫ����ID�����ֻ�ǵ�����ΪBasicFrequencyCounter
	 * 						ʹ�ã���ô�Ϳ�������Ϊnull��
	 * @param granularity	Ƶ��ͳ�ƵĿ����ȣ�����30��60��
	 * @param unit			�����ȵĵ�λ������TimeUnit.SECONDS��TimeUnit.MINUTES��
	 * @param purgePeriod	����ʱ��֮ǰ����ʷ������Ҫ�Զ����������1��3��
	 * @param purgeUnit		purgePeriod��ʱ�䵥λ������TimeUnit.HOURS��
	 */
	public FrequencyCounterDefinition(Object id, long granularity, TimeUnit unit,
			long purgePeriod, TimeUnit purgeUnit){
		this(id, granularity, unit);
		this.purgeBefore = TimeUnit.MILLISECONDS.convert(purgePeriod, purgeUnit);
	}

	/**
	 * ��ÿ�����
	 * @return the granularity
	 */
	public long getGranularity() {
		return granularity;
	}

	/**
	 * ��ñ�������ʱ��֮�ڵ���ʷ����
	 * @return �Ժ���Ϊ��λ�ı���ʱ�䳤��
	 */
	public long getPurgeBefore() {
		return purgeBefore;
	}

	/**
	 * ���ID
	 * @return the id
	 */
	public Object getId() {
		return id;
	}
}
