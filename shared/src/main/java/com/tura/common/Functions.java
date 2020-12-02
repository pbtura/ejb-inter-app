package com.tura.common;



import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.beanutils.PropertyUtils;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tura.common.annotations.NamedConstructor;


/**
 * Place for static utility functions
 * 
 * @author gdunkle
 * 
 */
public class Functions {
	private static String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	private static String NUMERIC_CHARS = "0123456789";
	private static Log log = LogFactory.getLog(Functions.class);

	/**
	 * Really just used for testing allows you to pull a list of specified fields
	 * from a list of objects ie get all the ids from a list of entities or dtos
	 * 
	 * @param objects
	 * @param fieldName
	 * @return
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	public static List extractPropertyValues(Collection<?> objects, String fieldName) {
		try {
			List<Object> fields = new ArrayList<Object>();
			for (Object o : objects) {
				if (o != null) {
					fields.add(get(o, fieldName));
				}
			}
			return fields;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static List<String> extractPropertyValuesAsString(Collection<?> objects, String fieldName) {
		try {
			List<String> fields = new ArrayList<String>();
			for (Object o : objects) {
				if (o != null) {
					Object value = get(o, fieldName);
					fields.add(value != null ? value.toString() : null);
				}
			}
			return fields;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Set extractPropertyValuesAsSet(Collection<?> objects, String fieldName) {
		try {
			Set<Object> fields = new HashSet<Object>();
			for (Object o : objects) {
				if (o != null) {
					fields.add(get(o, fieldName));
				}
			}
			return fields;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isNullableValueEqual(Object val1, Object val2) {
		if (val1 != null && val2 == null) {
			return false;
		} else if (val1 == null && val2 != null) {
			return false;
		} else if (val1 == null && val2 == null) {
			return true;
		} else {
			return (val1.equals(val2));
		}
	}

	public static boolean isNullableValueEqual(Object val1, Object val2, boolean trim) {
		Object str1 = val1;
		Object str2 = val2;
		if (trim == true) {
			if (val1 != null && val1 instanceof String) {
				str1 = ((String) val1).trim();
			}
			if (val2 != null && val2 instanceof String) {
				str2 = ((String) val2).trim();
			}
		}
		return isNullableValueEqual(str1, str2);
	}

	public static <T extends Comparable<T>> int compareNullableValue(T val1, T val2) {
		if (val1 != null && val2 == null) {
			return -1;
		} else if (val1 == null && val2 != null) {
			return 1;
		} else if (val1 == null && val2 == null) {
			return 0;
		} else {
			return val1.compareTo(val2);
		}
	}

	/**
	 * 
	 * @param d1
	 * @param d2
	 * @return Checks if two Dates are equal using only the date information
	 *         (ignores time).
	 */
	public static boolean datesEqual(Date d1, Date d2) {
		return compareDates(d1, d2) == 0;
	}

	public static boolean datesEqual(SimpleDateFormat dateFormat, Date d1, Date d2) {
		if (d1 != null && d2 == null) {
			return false;
		} else if (d1 == null && d2 != null) {
			return false;
		} else if (d1 == null && d2 == null) {
			return true;
		} else if (dateFormat.format(d1).equals(dateFormat.format(d2))) {
			return true;
		} else {
			return false;
		}
	}

	public static int compareDates(Date d1, Date d2) {
		if (d1 != null && d2 == null) {
			return 1;
		} else if (d1 == null && d2 != null) {
			return -1;
		} else if (d1 == null && d2 == null) {
			return 0;
		} else {
			return clearTimeFields(d1).compareTo(clearTimeFields(d2));
		}
	}

	public static <T> boolean contains(T[] array, T t) {
		for (T x : array) {
			if (x.equals(t)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean containsAny(Collection<T> testFor, Collection<T> containedIn) {
		for (T t : testFor) {
			if (containedIn.contains(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the first object in the testFor collection which is contained in the
	 * containedId collection return null if not found.
	 * 
	 * @param <T>
	 * @param testFor
	 * @param containedIn
	 * @return
	 */
	public static <T> T testForContainedIn(Collection<T> testFor, Collection<T> containedIn, Equals<T> eq) {
		for (T t1 : testFor) {
			for (T t2 : containedIn) {
				if (eq.equals(t1, t2)) {
					return t1;
				}
			}
		}
		return null;
	}

	public static interface Equals<T> {
		public boolean equals(T t1, T t2);
	}

	/**
	 * Generates 2 new string arrays such that corresponding indexes represent all
	 * the possible combinations of items from the two input arrays.
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static String[][] evenOutArrays(String[] array1, String[] array2) {
		if (array1 != null && array2 != null && array1.length != array2.length) {
			int newArraySize = array1.length * array2.length;
			String[] newArray1 = new String[newArraySize];
			String[] newArray2 = new String[newArraySize];
			int arrayOneIndex = 0;
			int arraytwoIndex = 0;
			for (int j = 0; j < newArray2.length; j++) {
				if (arrayOneIndex + 1 > array1.length) {
					arrayOneIndex = 0;
				}
				newArray1[j] = array1[arrayOneIndex++];
				if (arraytwoIndex + 1 > array2.length) {
					arraytwoIndex = 0;
				}
				newArray2[j] = array2[arraytwoIndex++];
			}
			array1 = newArray1;
			array2 = newArray2;
		}
		return new String[][] { array1, array2 };
	}

	public static String join(Collection<?> s, String delimiter) {
		return join(s, null, null, delimiter, null);
	}

	// How should this method behave if null is passed in for the delimiter?
	// Also, needs to handle a null value being passed in for the collection.
	public static String join(Collection<?> s, String prefix, String suffix, String delimiter, String field) {
		if (s == null || s.isEmpty()) {
			return "";
		} else if (delimiter == null) {
			throw new IllegalArgumentException("Join Failed. Null value passed in for delimiter.");
		}
		StringBuffer buffer = new StringBuffer();
		Iterator iter = s.iterator();
		while (iter.hasNext()) {
			Object value = iter.next();
			if (field != null) {
				try {
					value = PropertyUtils.getProperty(value, field);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (value.getClass().isArray()) {
				buffer.append(join((Object[]) value, prefix, suffix, delimiter, field));
				if (iter.hasNext()) {
					buffer.append(delimiter);
				}
			} else if (value instanceof Collection) {
				buffer.append(join((Collection) value, prefix, suffix, delimiter, field));
				if (iter.hasNext()) {
					buffer.append(delimiter);
				}
			} else {
				if (prefix != null) {
					buffer.append(prefix);
				}
				buffer.append(value);
				if (suffix != null) {
					buffer.append(suffix);
				}
				if (iter.hasNext()) {
					buffer.append(delimiter);
				}
			}
		}
		return buffer.toString();
	}

	public static String join(Object[] s, String prefix, String suffix, String delimiter, String field) {
		return join(Arrays.asList(s), prefix, suffix, delimiter, field);
	}

	public static Throwable getRootCause(Throwable e) {
		Throwable cause = e.getCause();
		if (cause == null || cause.equals(e)) {
			return e;
		} else {
			return getRootCause(cause);
		}
	}

	public static Object get(Object instance, String fieldName)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getFieldFromBean(instance.getClass(), fieldName);
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		return field.get(instance);
	}

	public static Field getFieldFromBean(Class clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() != null) {
				return getFieldFromBean(clazz.getSuperclass(), fieldName);
			}
			throw e;
		}
	}

	public static Field[] getAllFieldsFromBean(Class clazz) {
		List<Field> fields = new ArrayList<Field>();
		Field[] declaredFields = clazz.getDeclaredFields();
		fields.addAll(Arrays.asList(declaredFields));
		if (!clazz.isInterface() && clazz.getSuperclass() != null) {
			Field[] inheritedDeclaredFields = getAllFieldsFromBean(clazz.getSuperclass());
			for (Field field : inheritedDeclaredFields) {
				if (!fields.contains(field)) {
					fields.add(field);
				}
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Class<?>, A extends Annotation> Constructor<T> findNamedConstructor(T clazz, String name) {
		for (Constructor<?> constructor : clazz.getConstructors()) {
			NamedConstructor namedConstructor = constructor.getAnnotation(NamedConstructor.class);
			if (namedConstructor != null && namedConstructor.value().equals(name)) {
				return (Constructor<T>) constructor;
			}
		}
		return null;
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.trim().length() == 0;
	}

	/**
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 * 
	 *         Compare two BigDecimal objects for equality. Scale is ignored so
	 *         assertEqual(21.0, 21.000) should return true.
	 */
	public static boolean isBigDecimalEqual(BigDecimal obj1, BigDecimal obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		} else if (obj1 != null && obj2 != null) {
			if (obj1.compareTo(obj2) == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isBigDecimalGreaterThanOrEqual(BigDecimal obj1, BigDecimal obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		} else if (obj1 != null && obj2 != null) {
			if (obj1.compareTo(obj2) >= 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isBigDecimalLessThanOrEqual(BigDecimal obj1, BigDecimal obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		} else if (obj1 != null && obj2 != null) {
			if (obj1.compareTo(obj2) <= 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isNullOrEmpty(Collection<?> value) {
		return value == null || value.isEmpty();
	}

	public static String randomString(int size) {
		StringBuffer randomstring = new StringBuffer();
		for (int i = 0; i < size; i++) {
			Double rnum = new Double(Math.floor(Math.random() * CHARS.length()));
			randomstring.append(CHARS.substring(rnum.intValue(), rnum.intValue() + 1));
		}
		return randomstring.toString();
	}

	/**
	 * 
	 * @param min : must be >=0 and != max
	 * @param max : must be >=1 and != min
	 * @return random int between min and max
	 */
	public static int randomInt(int min, int max) {
		int result = RandomUtils.nextInt(min, max);
		return result;
	}

	public static BeanManager getBeanManager() {
		try {
			Context ctx = new InitialContext();
			BeanManager beanManager = (BeanManager) ctx.lookup("java:comp/BeanManager");
			if (log.isDebugEnabled()) {
				log.debug("Using " + beanManager);
			}
			return beanManager;
		} catch (NamingException e) {
			throw new RuntimeException("Problem getting reference to BeanManager", e);
		}
	}

	public static void logTransactionStatus(String from, UserTransaction ut) throws SystemException {
		if (log.isTraceEnabled()) {
			log.trace("Logging tx from " + (from != null ? "Unknown" : from));
			switch (ut.getStatus()) {
			case Status.STATUS_ACTIVE:
				log.trace("UserTransaction status: STATUS_ACTIVE");
				break;
			case Status.STATUS_COMMITTED:
				log.trace("UserTransaction status: STATUS_COMMITTED");
				break;
			case Status.STATUS_COMMITTING:
				log.trace("UserTransaction status: STATUS_COMMITTING");
				break;
			case Status.STATUS_MARKED_ROLLBACK:
				log.trace("UserTransaction status: STATUS_MARKED_ROLLBACK");
				break;
			case Status.STATUS_NO_TRANSACTION:
				log.trace("UserTransaction status: STATUS_NO_TRANSACTION");
				break;
			case Status.STATUS_PREPARED:
				log.trace("UserTransaction status: STATUS_PREPARED");
				break;
			case Status.STATUS_PREPARING:
				log.trace("UserTransaction status: STATUS_PREPARING");
				break;
			case Status.STATUS_ROLLEDBACK:
				log.trace("UserTransaction status: STATUS_ROLLEDBACK");
				break;
			case Status.STATUS_ROLLING_BACK:
				log.trace("UserTransaction status: STATUS_ROLLING_BACK");
				break;
			case Status.STATUS_UNKNOWN:
				log.trace("UserTransaction status: STATUS_UNKNOWN");
				break;
			default:
				break;
			}
		}
	}

	public static void rollbackIfActive(UserTransaction tx) {
		if (tx != null) {
			try {
				if (tx.getStatus() == Status.STATUS_ACTIVE) {
					log.debug("rollbackIfActive");
					tx.rollback();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static <R> R quietRollback(UserTransaction tx, R r) {
		if (tx != null) {
			try {
				logTransactionStatus(getCallerMethod(), tx);
				switch (tx.getStatus()) {
				case Status.STATUS_NO_TRANSACTION:
					break;
				case Status.STATUS_ROLLEDBACK:
					break;
				case Status.STATUS_ROLLING_BACK:
					break;
				default:
					tx.rollback();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return r;
	}

	public static void beginTx(UserTransaction tx) throws NotSupportedException, SystemException {
		tx.begin();
		if (log.isTraceEnabled()) {
			log.trace(getCallerMethod() + " beginning TX - " + tx.toString());
		}
	}

	public static void commitTx(UserTransaction tx) throws SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, SystemException {
		if (log.isTraceEnabled()) {
			log.trace(getCallerMethod() + " committing TX - " + tx.toString());
		}
		tx.commit();
	}

	public static void rollbackTx(UserTransaction tx) throws SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
		if (log.isTraceEnabled()) {
			log.trace(getCallerMethod() + " rolling back TX - " + tx.toString());
		}
		tx.rollback();
	}

	/*
	 * This is for tracing errrors only! Otherwise this sort of thing is a bad idea
	 */
	public static String getCallerMethod() {
		if (log.isTraceEnabled()) {
			StackTraceElement[] stack = new Throwable().getStackTrace();
			StackTraceElement ste = null;
			if (stack.length > 1) {
				for (StackTraceElement s : stack) {
					if (!s.getClassName().equals(Functions.class.getName())) {
						ste = s;
						break;
					}
				}
				return ste == null ? "[Unavailable]"
						: ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber() + ")";
			}
		}
		return null;
	}

	public static void quietRollback(UserTransaction tx) {
		if (tx != null) {
			try {
				logTransactionStatus(getCallerMethod(), tx);
				switch (tx.getStatus()) {
				case Status.STATUS_NO_TRANSACTION:
					break;
				case Status.STATUS_ROLLEDBACK:
					break;
				case Status.STATUS_ROLLING_BACK:
					break;
				default:
					tx.rollback();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static abstract class PropertyChangeEventSetter<T> {
		protected String propertyName;
		protected Object oldValue;
		protected Object newValue;
		protected T source;

		public PropertyChangeEventSetter(T source, String propertyName, Object oldValue, Object newValue) {
			super();
			this.source = source;
			this.propertyName = propertyName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		protected T getThis() {
			return source;
		}

		protected PropertyChangeEvent getEvent() {
			return new PropertyChangeEvent(source, propertyName, oldValue, newValue);
		}

		public abstract PropertyChangeEvent set();
	}

	public static <T, C extends Collection<T>> Set<T> union(C array1, C array2) {
		if ((array1 == null || array1.isEmpty()) && (array2 == null || array2.isEmpty())) {
			return null;
		} else if (array1 == null || array1.isEmpty()) {
			return new HashSet<T>(array2);
		} else if (array2 == null || array2.isEmpty()) {
			return new HashSet<T>(array1);
		} else {
			Set<T> union = new HashSet<T>(array1);
			union.addAll(array2);
			return union;
		}
	}

	/**
	 * Extracts the month value from a Date without using the deprecated
	 * Date.getMonth() method.
	 * 
	 * @return
	 */
	public static int getMonthFromDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

	/**
	 * Extracts the year value from a Date without using the deprecated
	 * Date.getYear() method.
	 * 
	 * @return
	 */
	public static int getYearFromDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	/**
	 * Constructs a new Date object using the year, month, and date passed in
	 * 
	 * @return Date
	 */
	public static Date createNewDate(int year, int month, int date) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month, date);
		return c.getTime();
	}

	/**
	 * Create a copy of the Date passed in with all fields other than year, month,
	 * and date reset to 0.
	 * 
	 * @param origDate
	 * @return
	 */
	public static Date clearTimeFields(Date origDate) {
		if (origDate != null) {
			Calendar orig = Calendar.getInstance();
			orig.clear();
			orig.setTime(origDate);
			Calendar c = Calendar.getInstance();
			c.clear();
			c.set(Calendar.YEAR, orig.get(Calendar.YEAR));
			c.set(Calendar.MONTH, orig.get(Calendar.MONTH));
			c.set(Calendar.DAY_OF_MONTH, orig.get(Calendar.DAY_OF_MONTH));
			Date d = c.getTime();
			return d;
		} else {
			return null;
		}
	}

	public static Date incrementDate(Date origDate, int days) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(origDate);
		c.add(Calendar.DATE, days);
		return c.getTime();
	}

	/**
	 * Null safe trim
	 * 
	 * @param value
	 * @return
	 */
	public static String trim(String value) {
		return value != null ? value.trim() : null;
	}



	public static class CharArrayToDouble {
		private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00.00");

		public static Double toDouble(String stringValue) {
			try {
				char[] value = stringValue.trim().toCharArray();
				char[] normalizedArray;
				switch (value.length) {
				case 0:
					normalizedArray = new char[] { '0' };
					break;
				case 1:
					normalizedArray = new char[] { '0', '.', '0', value[0] };
					break;
				case 2:
					normalizedArray = new char[] { '0', '.', value[0], value[1] };
					break;
				case 3:
					normalizedArray = new char[] { value[0], '.', value[1], value[2] };
					break;
				case 4:
					normalizedArray = new char[] { value[0], value[1], '.', value[2], value[3] };
					break;
				case 5:
					normalizedArray = new char[] { value[0], value[1], value[2], '.', value[3], value[4] };
					break;
				case 6:
					normalizedArray = new char[] { value[0], value[1], value[2], value[3], '.', value[4], value[5] };
					break;
				default:
					throw new RuntimeException("Could not convert " + value + " to double");
				}
				return (Double) DECIMAL_FORMAT.parse(new String(normalizedArray)).doubleValue();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		public static char[] fromDouble(Double value) {
			String s = DECIMAL_FORMAT.format(value);
			s.replace(".", "");
			return s.toCharArray();
		}
	}

	

	public static boolean isSubClassOf(Class clazz, Class superClazz, Class root) {
		if (clazz == null || clazz == root || clazz == Object.class || clazz.isPrimitive()) {
			return false;
		} else if (clazz == superClazz) {
			return true;
		} else {
			return isSubClassOf(clazz.getSuperclass(), superClazz, root);
		}
	}

	public static List<Field> getAllDeclaredFields(Class clazz, List<Field> fields) {
		return getAllDeclaredFields(clazz, fields, Object.class);
	}

	public static List<Field> getAllDeclaredFields(Class clazz, List<Field> fields, Class root) {
		if (clazz != null && !clazz.isPrimitive() && root.isAssignableFrom(clazz)
				&& clazz.getDeclaredFields().length > 0) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			if (!clazz.equals(root)) {
				return getAllDeclaredFields(clazz.getSuperclass(), fields);
			}
		}
		return fields;
	}

}
