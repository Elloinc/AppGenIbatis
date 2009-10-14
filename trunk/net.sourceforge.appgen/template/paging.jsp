<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
	<a href="javascript:goPage('${paging.firstPage}');">first</a> | 
	<a href="javascript:goPage('${paging.prelinkPage}');">pre</a> | 
	<c:forEach var="page" begin="${paging.startPagingLink}" end="${paging.endPagingLink}">
		<c:choose>
			<c:when test="${page == paging.currentPage}">
				<b>${page}</b> |
			</c:when>
			<c:otherwise>
				<a href="javascript:goPage('${page}');">${page}</a> |
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<a href="javascript:goPage('${paging.postlinkPage}');">next</a> | 
	<a href="javascript:goPage('${paging.lastPage}');">last</a>