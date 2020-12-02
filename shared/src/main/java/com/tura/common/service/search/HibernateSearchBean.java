package com.tura.common.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;

import com.tura.common.Functions;
import com.tura.common.domain.Entity;

/**
 * Implementation of service for creating searchable lucene indexes for entities
 * 
 * @author gdunkle
 * 
 */
// a custom pool defined in standalone.xml
//@Pool(value = "common-pool")
public class HibernateSearchBean implements SearchService {

	@PersistenceContext(name = "entityManager")
	protected EntityManager entityManager;
	protected static Log log = LogFactory.getLog(SearchService.class);
	@Resource
	protected SessionContext context;
	protected static Version LUCENE_VERSION = Version.LUCENE_5_5_5;

	public HibernateSearchBean(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	public HibernateSearchBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Boolean emExists() {

		return entityManager != null;
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String queryString, String... fields)
			throws ParseException {
		if (!matchExact) {
			if (!queryString.trim().equals("") && !queryString.endsWith("*")) {
				queryString = queryString + "*";
			}
		}
		return search(type, queryString, fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, String queryString, String... fields)
			throws ParseException {
		return search(type, new String[] { queryString }, Occur.SHOULD, fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String[] queries, Occur defaultBoolean,
			String... fields) throws ParseException {
		String[] updatedQueries = appendWildcard(queries, matchExact);
		return search(type, updatedQueries, defaultBoolean, fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String[] queries, Occur defaultBoolean,
			SortField[] sortBy, String... fields) throws ParseException {
		String[] updatedQueries = appendWildcard(queries, matchExact);
		return search(type, updatedQueries, defaultBoolean, sortBy, fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, String[] queries, Occur defaultBoolean, String... fields)
			throws ParseException {
		return search(type, queries, defaultBoolean, new SortField[0], fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, String[] queries, Occur defaultBoolean, SortField[] sortBy,
			String... fields) throws ParseException {
		if (queries.length != fields.length) {
			String[][] results = Functions.evenOutArrays(queries, fields);
			queries = results[0];
			fields = results[1];
		}
		BooleanClause.Occur[] booleans = new BooleanClause.Occur[fields.length];
		int i = 0;
		for (String field : fields) {
			booleans[i++] = defaultBoolean;
		}
		return search(type, queries, booleans, sortBy, fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, boolean matchExact, String[] queries,
			BooleanClause.Occur[] booleans, String... fields) throws ParseException {
		String[] updatedQueries = appendWildcard(queries, matchExact);
		return search(type, updatedQueries, booleans, fields);
	}

	public <E extends Entity> List<E> search(Class<E> type, String[] queries, BooleanClause.Occur[] booleans,
			String... fields) throws ParseException {
		return search(type, queries, booleans, new SortField[0], fields);
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> type, String[] queries, BooleanClause.Occur[] booleans,
			SortField[] sortBy, String... fields) throws ParseException {
		org.apache.lucene.search.BooleanQuery mainQuery = new BooleanQuery();
		org.apache.lucene.search.Query subQuery = null;
		subQuery = MultiFieldQueryParser.parse(queries, fields, booleans, new StandardAnalyzer());
		mainQuery.add(subQuery, BooleanClause.Occur.SHOULD);
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(mainQuery, type);
		if (sortBy != null && sortBy.length > 0) {
			Sort sort = new Sort();
			sort.setSort(sortBy);
			fullTextQuery.setSort(sort);
		}
		return (List<E>) fullTextQuery.getResultList();
	}

	@Override
	public String searchForIds(Class<? extends Entity> type, String queryString, String... fields)
			throws ParseException {
		List<Object[]> results = search(type, new String[] { queryString }, Occur.SHOULD, fields,
				new String[] { "id" });
		if (results.isEmpty()) {
			return "";
		}
		return Functions.join(results, null, null, ",", null);
	}

	@Override
	public List<Object[]> search(Class<? extends Entity> type, boolean matchExact, String[] queries,
			BooleanClause.Occur defaultBoolean, String[] projections, String... fields) throws ParseException {
		String[] updatedQueries = appendWildcard(queries, matchExact);
		return search(type, updatedQueries, defaultBoolean, projections, fields);
	}

	@Override
	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur defaultBoolean,
			String[] projections, SortField[] sortBy, String... fields) throws ParseException {
		if (queries.length != fields.length) {
			String[][] results = Functions.evenOutArrays(queries, fields);
			queries = results[0];
			fields = results[1];
		}
		BooleanClause.Occur[] booleans = new BooleanClause.Occur[fields.length];
		int i = 0;
		for (String field : fields) {
			booleans[i++] = defaultBoolean;
		}
		return search(type, queries, booleans, projections, sortBy, fields);
	}

	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur defaultBoolean,
			String[] projections, String... fields) throws ParseException {
		return search(type, queries, defaultBoolean, projections, null, fields);
	}

	@Override
	public List<Object[]> search(Class<? extends Entity> type, boolean matchExact, String[] queries,
			BooleanClause.Occur[] booleans, String[] projections, String... fields) throws ParseException {
		String[] updatedQueries = appendWildcard(queries, matchExact);
		return search(type, updatedQueries, booleans, projections, fields);
	}

	@Override
	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur[] booleans,
			String[] projections, String... fields) throws ParseException {
		return search(type, queries, booleans, projections, null, fields);
	}

	@Override
	public List<Object[]> search(Class<? extends Entity> type, boolean matchExact, String[] queries,
			Occur defaultBoolean, String[] projections, SortField[] sortBy, String... fields) throws ParseException {
		String[] updatedQueries = appendWildcard(queries, matchExact);
		return search(type, updatedQueries, defaultBoolean, projections, sortBy, fields);
	}

	@Override
	public List<Object[]> search(Class<? extends Entity> type, String[] queries, BooleanClause.Occur[] booleans,
			String[] projections, SortField[] sortBy, String... fields) throws ParseException {
		org.apache.lucene.search.BooleanQuery mainQuery = new BooleanQuery();
		org.apache.lucene.search.Query subQuery = null;
		subQuery = MultiFieldQueryParser.parse(queries, fields, booleans, new StandardAnalyzer());
		mainQuery.add(subQuery, BooleanClause.Occur.SHOULD);
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(mainQuery, type);
		fullTextQuery.setProjection(projections);
		if (sortBy != null && sortBy.length > 0) {
			Sort sort = new Sort();
			sort.setSort(sortBy);
			fullTextQuery.setSort(sort);
		}
		return (List<Object[]>) fullTextQuery.getResultList();
	}

	public List<String> fieldSearch(Class<? extends Entity> type, String field, String query)
			throws ParseException, IOException {
		long start = System.currentTimeMillis();
		FullTextSession fullTextSession = org.hibernate.search.Search
				.getFullTextSession((Session) entityManager.getDelegate());
		SearchFactory searchFactory = fullTextSession.getSearchFactory();
		IndexReader reader = searchFactory.getIndexReaderAccessor().open(type);
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(field, new StandardAnalyzer());
			org.apache.lucene.search.Query q = parser.parse(query);
			TopDocs topDocs = indexSearcher.search(q, 100);
			List<String> results = new ArrayList<String>();
			if (topDocs.totalHits > 0) {
				for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
					String value = indexSearcher.doc(scoreDoc.doc).get(field);
					if (!results.contains(value)) {
						results.add(value);
					}
				}
			}
			return results;
		} finally {
			searchFactory.getIndexReaderAccessor().close(reader);
			long stop = System.currentTimeMillis();
			if (log.isDebugEnabled()) {
				log.debug("Search for " + query + " took: " + (stop - start) + " ms");
			}
		}
	}

	@Override
	@Deprecated
	public void indexType(String classname) {
		indexType(classname, false);
	}

	@Override
	@Deprecated
	public void indexType(Class<? extends Entity> type) {
		indexType(type, false);
	}

	@Override
	public void indexType(String classname, Boolean async) {
		try {
			indexType((Class<? extends Entity>) Class.forName(classname), async);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void indexType(Class<? extends Entity> type, Boolean async) {
		indexType(type, 0, null, null, async);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Asynchronous
	public void indexType(Class<? extends Entity> type, Integer startIndex, Integer pageSize, Long total,
			Boolean async) {
		indexType(type, startIndex, pageSize, total, async, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Asynchronous
	public void indexType(Class<? extends Entity> type, Integer startIndex, Integer pageSize, Long total, Boolean async,
			Boolean clearOld) {
		FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
		// before we update the indexes we first want to purge any old indexes for the
		// entity
		if (clearOld) {
			ftem.purgeAll(type);
			// we only want to clear the old indexes once
			clearOld = false;
		}
		if (pageSize == null) {
			Long count = (Long) entityManager.createQuery("select count(e) from " + type.getName() + " as e")
					.getSingleResult();
			Integer currentPage = 0;
			while (currentPage <= count) {
				if (async) {
					context.getBusinessObject(SearchService.class).indexType(type, currentPage, 30, count, async,
							clearOld);
				} else {
					indexType(type, currentPage, 30, count, async, clearOld);
				}
				currentPage += 30;
			}
		} else {
			List<? extends Entity> results = (List<? extends Entity>) entityManager
					.createQuery("from " + type.getName()).setFirstResult(startIndex).setMaxResults(pageSize)
					.getResultList();
			if (!results.isEmpty()) {
				// loop through the results and add invoke the indexed methods
				// and
				// add the values to the index
				// document
				for (Entity result : results) {
					ftem.index(result);
				}
				ftem.flushToIndexes(); // apply changes to indexes
				ftem.clear(); // clear since the queue is processed
			}
			if (log.isInfoEnabled()) {
				log.info("Indexed " + startIndex + " - " + (startIndex + pageSize) + " of " + total + " for entity "
						+ type.getName());
			}
		}
	}

	protected String[] appendWildcard(String[] queries, boolean matchExact) {
		for (int i = 0; i < queries.length; i++) {
			if (!matchExact) {
				String queryString = queries[i];
				if (!queryString.trim().equals("") && !queryString.endsWith("*")) {
					queryString = queryString + "*";
					queries[i] = queryString;
				}
			}
		}
		return queries;
	}

	
}
