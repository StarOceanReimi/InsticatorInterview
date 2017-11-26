<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Question Simple Manager</title>
</head>
<body>
	<h1>Question Manager</h1>
	<hr/>
	<div id="main">
		<div><button onclick="goDetail(0)">Add New Question</button></div>
		<div><label for="searchInput">Tags Or Title For Search: </label> <input id="searchInput" name="search" /><button>Search</button></div>
		<c:set var="qcount" value="0" scope="page"></c:set>
		<c:forEach items="${questions}" var="q">
			<c:set var="qcount" value="${ qcount + 1 }" scope="page"></c:set>
			<div class="question-block-wrapper" data-question-id="${ q.id }" data-question-type="${ q.type }">
				<div class="command-pane">
					<button onclick="goDetail(${ q.id })">Edit</button>
					<button onclick="doThis(this)">Delete</button>
					<button onclick="doThis(this)">Submit</button>
				</div>
				<div class="question-block">
					<div class="question-title"><span>${ qcount }. </span>${ q.title }</div>
					<div class="question-tag-wrapper">
						<c:if test="${ q.tags != null }">
						TAGS: 
							<c:forEach items="${ q.tags }" var="tag">
								<span class="tag" data-tag-id="${ tag.id }">${ tag.name }</span>
							</c:forEach>
						</c:if>
					</div>
					<div class="option-group">
						<table>
							<c:if test="${ q.index.name != null }">
								<tr>
									<th>${ q.index.name }</th>
									<c:if test="${ q.column != null && q.column.name != null}">
										<th>${ q.column.name }</th>	
									</c:if>		
								</tr>
							</c:if>
							<c:forEach items="${ q.index.options }" var="row">
								<tr>
									<td>
										<c:choose>
											<c:when test="${ q.column == null }">
												<input id="option-${ row.id }" data-option-id="${ row.id }" type="checkbox"/>
												<label for="option-${ row.id }">${ row.name }</label>
											</c:when>
											<c:otherwise>
												${ row.name }
											</c:otherwise>
										</c:choose>
									</td>
									<c:forEach items="${ q.column.options }" var="col">
										<td>
											<input id="option-${ row.id }-${ col.id }" data-option-id="${ row.id }-${ col.id }" type="checkbox"/>
											<label for="option-${ row.id }-${ col.id }">${ col.name }</label>
										</td>
									</c:forEach>		
								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
			</div>
		</c:forEach>
		
	
	</div>
	<script src="https://code.jquery.com/jquery-1.12.4.min.js"
	  integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ="
	  crossorigin="anonymous"></script>
	
	<script type="text/javascript" src="/js/app.js"></script>
</body>
</html>