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
package net.sf.jabb.util.col;

import java.io.Serializable;

/**
 * Encapsulates multiple Comparable (implements Comparable Interface) values into one object, which
 * is suitable to be used as key object of Map.<br>
 * 把多个可比较（实现了Comparable）值封装在这一个对象里，适合用来作为Map的key。
 * <p>
 * It supports hashCode(), toString(), equals(), compareTo() methods.
 * <p>
 * 它支持hashCode(), toString(), equals(), compareTo()方法。
 * <p>
 * Classes that are Comparable include:<br>
 * 可比较（实现了Comparable）的类包括：
 * <p>
 *     Authenticator.RequestorType, BigDecimal, BigInteger, Boolean, Byte, ByteBuffer, Calendar, Character, CharBuffer, Charset, ClientInfoStatus, CollationKey, Component.BaselineResizeBehavior, CompositeName, CompoundName, Date, Date, Desktop.Action, Diagnostic.Kind, Dialog.ModalExclusionType, Dialog.ModalityType, Double, DoubleBuffer, DropMode, ElementKind, ElementType, Enum, File, Float, FloatBuffer, Formatter.BigDecimalLayoutForm, FormSubmitEvent.MethodType, GregorianCalendar, GroupLayout.Alignment, IntBuffer, Integer, JavaFileObject.Kind, JTable.PrintMode, KeyRep.Type, LayoutStyle.ComponentPlacement, LdapName, Long, LongBuffer, MappedByteBuffer, MemoryType, MessageContext.Scope, Modifier, MultipleGradientPaint.ColorSpaceType, MultipleGradientPaint.CycleMethod, NestingKind, Normalizer.Form, ObjectName, ObjectStreamField, Proxy.Type, Rdn, Resource.AuthenticationType, RetentionPolicy, RoundingMode, RowFilter.ComparisonType, RowIdLifetime, RowSorterEvent.Type, Service.Mode, Short, ShortBuffer, SOAPBinding.ParameterStyle, SOAPBinding.Style, SOAPBinding.Use, SortOrder, SourceVersion, SSLEngineResult.HandshakeStatus, SSLEngineResult.Status, StandardLocation, String, SwingWorker.StateValue, Thread.State, Time, Timestamp, TimeUnit, TrayIcon.MessageType, TypeKind, URI, UUID, WebParam.Mode, XmlAccessOrder, XmlAccessType, XmlNsForm
 * <br>
 * <br> and {@link IntegerArray}, {@link LongArray}, {@link NumberArray}.
 * 
 * @author Zhengmao HU (James)
 *
 */
@SuppressWarnings("rawtypes")
public class ComparableArray implements Comparable<ComparableArray>, Serializable{
	private static final long serialVersionUID = 2978159654591722222L;

	protected Comparable[] values;
	
	/**
	 * Constructs an instance that encapsulates specified Comparable values.<br>
	 * 创建一个包含这些可比较值的实例。
	 * 
	 * @param values	Comparable values that will be encapsulated.<br>
	 * 				一批将被封装的可比较值。
	 */
	public ComparableArray(Comparable... values){
		this.values = values;
	}
	
	/**
	 * Gets all the values encapsulated in this object.<br>
	 * 获得所有值。
	 * 
	 * @return	array of values encapsulated.
	 */
	public Object[] getValues(){
		return values;
	}
	
	/**
	 * Gets the value in specified position.<br>
	 * 获得指定位置的值。
	 * 
	 * @param index	position (position of the first one is 0)
	 * @return	the value
	 */
	public Object getValue(int index){
		return values[index];
	}
	
	/**
	 * Gets the calculated hash code.<br>
	 * 获得计算得到的hash值。
	 */
	@Override
	public int hashCode(){
		long result = 0;
		for (Object o: values){
			long h = o.hashCode();
			result += h << 32;
			result ^= h * 31;
		}
		return (int) result;
	}

	@SuppressWarnings("unchecked")
	public int compareTo(ComparableArray to) {
		if (this.values.length < to.values.length){
			return -1;
		}else if (this.values.length > to.values.length){
			return 1;
		}
		
		int result = 0;
		int i = 0;
		while (result == 0 && i < this.values.length){
			if (this.values[i].compareTo(to.values[i]) < 0){
				result = -1;
			}else if (this.values[i].compareTo(to.values[i]) > 0){
				result = 1;
			}
			i ++;
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null){
			return false;
		}
		//check for self-comparison
	    if ( this == obj ) 
	    	return true;
	    
	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explict check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)

	    if ( !(obj instanceof ComparableArray) ) 
	    	return false;

		return compareTo((ComparableArray)obj) == 0;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		boolean isFirst = true;
		for (Object o: values){
			if (isFirst){
				isFirst = false;
			}else{
				sb.append(", ");
			}
			sb.append(o.toString());
		}
		sb.append(')');
		return sb.toString();
	}

}
