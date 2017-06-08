package net.yuanmomo.springboot.util;

import java.util.List;

public class PaginationBean<T> {
	private long pageNum;
	
	private long numPerPage;
	
	private long totalCount;
	
	private long totalPages;
	
	private List<T> result;

	/**
	 * totalPages.
	 *
	 * @return  the totalPages
	 * @since   JDK 1.6
	 */
	public long getTotalPages() {
		return totalPages;
	}

	/**
	 * totalPages.
	 *
	 * @param   totalPages    the totalPages to set
	 * @since   JDK 1.6
	 */
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * totalCount.
	 *
	 * @return  the totalCount
	 * @since   JDK 1.6
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * totalCount.
	 *
	 * @param   totalCount    the totalCount to set
	 * @since   JDK 1.6
	 */
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * pageNum.
	 *
	 * @return  the pageNum
	 * @since   JDK 1.6
	 */
	public long getPageNum() {
		return pageNum;
	}

	/**
	 * pageNum.
	 *
	 * @param   pageNum    the pageNum to set
	 * @since   JDK 1.6
	 */
	public void setPageNum(long pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * numPerPage.
	 *
	 * @return  the numPerPage
	 * @since   JDK 1.6
	 */
	public long getNumPerPage() {
		return numPerPage;
	}

	/**
	 * numPerPage.
	 *
	 * @param   numPerPage    the numPerPage to set
	 * @since   JDK 1.6
	 */
	public void setNumPerPage(long numPerPage) {
		this.numPerPage = numPerPage;
	}

	/**
	 * result.
	 *
	 * @return  the result
	 * @since   JDK 1.6
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * result.
	 *
	 * @param   result    the result to set
	 * @since   JDK 1.6
	 */
	public void setResult(List<T> result) {
		this.result = result;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PaginationBean that = (PaginationBean) o;

		if (pageNum != that.pageNum) return false;
		if (numPerPage != that.numPerPage) return false;
		if (totalCount != that.totalCount) return false;
		if (totalPages != that.totalPages) return false;
		return result != null ? result.equals(that.result) : that.result == null;
	}

	@Override
	public int hashCode() {
		int result1 = (int) (pageNum ^ (pageNum >>> 32));
		result1 = 31 * result1 + (int) (numPerPage ^ (numPerPage >>> 32));
		result1 = 31 * result1 + (int) (totalCount ^ (totalCount >>> 32));
		result1 = 31 * result1 + (int) (totalPages ^ (totalPages >>> 32));
		return result1;
	}
}
