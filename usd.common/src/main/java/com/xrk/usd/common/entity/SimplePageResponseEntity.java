package com.xrk.usd.common.entity;

import java.util.List;

public class SimplePageResponseEntity<T>
{
	private List<T> result;
	private int total;
	private int pageSize;
	private int pageNum;
	
	public SimplePageResponseEntity(int total, int size, int num)
	{
		this.total = total;
		this.pageSize = size;
		this.pageNum = num;
	}
	
	public List<T> getResult()
	{
		return result;
	}

	public void setResult(List<T> result)
	{
		this.result = result;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getPageNum()
	{
		return pageNum;
	}

	public void setPageNum(int pageNum)
	{
		this.pageNum = pageNum;
	}
}
