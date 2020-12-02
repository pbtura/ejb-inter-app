package com.tura.common.service.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Sort;

import com.tura.common.domain.Entity;

/**
 * Service used for generating complete lucene indexes for a given entity
 * 
 * @author gdunkle
 * 
 */
public interface SearchService {
	
	public Boolean emExists();
	
	@Deprecated
	public void indexType(String classname);

	@Deprecated
	public void indexType(Class<? extends Entity> type);

	public void indexType(String classname, Boolean async);

	public void indexType(Class<? extends Entity> type, Boolean async);

	public void indexType(Class<? extends Entity> type, Integer startIndex, Integer pageSize, Long total,
			Boolean async);

	public void indexType(Class<? extends Entity> type, Integer startIndex, Integer pageSize, Long total, Boolean async,
			Boolean clearOld);

	public <E extends Entity> List<E> search(Class<E> type, String query, String... fields) throws ParseException;

	public String searchForIds(Class<? extends Entity> type, String query, String... fields) throws ParseException;

	public List<String> fieldSearch(Class<? extends Entity> type, String field, String query)
			throws ParseException, IOException;

	public <E extends Entity> List<E> search(Class<E> type, String[] queries, BooleanClause.Occur[] booleans,
			String... fields) throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, String[] queries, BooleanClause.Occur[] booleans,
			SortField[] sortBy, String... fields) throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, String[] queries, BooleanClause.Occur defaultBoolean,
			String... fields) throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, String[] queries, BooleanClause.Occur defaultBoolean,
			SortField[] sortBy, String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur[] booleans,
			String[] projections, String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur[] booleans,
			String[] projections, SortField[] sortBy, String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur defaultBoolean,
			String[] projections, String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur defaultBoolean,
			String[] projections, SortField[] sortBy, String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, boolean matchExact, String[] queries,
			BooleanClause.Occur defaultBoolean, String[] projections, SortField[] sortBy, String... fields)
			throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String queryString, String... fields)
			throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String[] queries, Occur defaultBoolean,
			String... fields) throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String[] queries, Occur defaultBoolean,
			SortField[] sortBy, String... fields) throws ParseException;

	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String[] queries, Occur[] booleans,
			String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, boolean matchExact, String[] queries,
			Occur defaultBoolean, String[] projections, String... fields) throws ParseException;

	public List<Object[]> search(Class<? extends Entity> type, boolean matchExact, String[] queries, Occur[] booleans,
			String[] projections, String... fields) throws ParseException;
}
