<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Question Detail</title>
<script type="text/javascript" src="/js/app.js"></script>
</head>
<body>
	<h1>Question Detail:</h1>
	<hr/>

	<div class="question-block">
		<div class="command-pane">
			<button onclick="">Submit</button>
		</div>
		<div class="question-title"><label>Question Title: </label><input name="q.title"  value="${ empty q.title ? '' : q.title }"/></div>
		<div class="question-type">
			<label>Question Type: </label>
			<select name="q.type">
				<option ${ q.type.ordinal() == 0 ? "selected" : "" }>TRIVIA</option>
				<option ${ q.type.ordinal() == 1 ? "selected" : "" }>POLL</option>
				<option ${ q.type.ordinal() == 2 ? "selected" : "" }>CHECKBOX</option>
				<option ${ q.type.ordinal() == 3 ? "selected" : "" }>MATRIX</option>
			</select>
		</div>
		<div class="question-tag-wrapper">
			<div class="command-pane-inner">
				<button>ADD TAGS</button>
			</div>
			<c:if test="${ not empty q.tags }">
				<c:forEach items="${ q.tags }" var="tag">
					<div class="tag" data-tag-id="${ tag.id }">
						<input value="${ tag.name }">
						<button>REMOVE</button>
					</div>
				</c:forEach>
			</c:if>
		</div>
		<div class="option-group">
			<div class="command-pane-inner">
				<button>ADD INDEX</button>
				<button>ADD COLUMN</button>
			</div>
			<table>
				<tr>
					<th>
						<c:if test="${ not empty q.index }">
							<label>Index Name</label><input name="q.index.name" value="${ empty q.index.name ? '' : q.index.name }">
						</c:if>
					</th>
					<th>
						<c:if test="${ not empty q.column }">
							<label>Column Name</label><input name="q.column.name" value="${ empty q.column.name ? '' : q.column.name }">
						</c:if>
					</th>
				</tr>
				<c:set var="colLen" value="${ empty q.column ? 0 : q.column.options.size() }" scope="page"></c:set>				
				<c:set var="idxLen" value="${ empty q.index ? 0 : q.index.options.size() }" scope="page"></c:set>
				<c:set var="length" value="${ colLen > idxLen ? colLen : idxLen }" scope="page"></c:set>
				<c:set var="cIter" value="${ empty q.column ? null : q.column.options.iterator() }" scope="page"></c:set>
				<c:set var="iIter" value="${ empty q.index ? null : q.index.options.iterator() }" scope="page"></c:set>
				<c:forEach var="cur" begin="0" end="${ length }" varStatus="vs">
					<tr>
						<td>
							<c:if test="${ not empty iIter and iIter.hasNext() }">
								<c:set var="idx" value="${ iIter.next() }" scope="page"></c:set>
								<label>Right Answer</label><input type="checkbox" data-optoin-id="${ idx.id  } name="q.index.options.suggested">
								<input name="q.index.options.name" data-optoin-id="${ idx.id  }" value="${ idx.name }">	
								<button>REMOVE</button>						
							</c:if>
						</td>
						<c:if test="${ not empty cIter and cIter.hasNext() }">
							<td>
								<c:set var="cdx" value="${ cIter.next() }" scope="page"></c:set>
								<label>Right Answer</label><input type="checkbox" data-optoin-id="${ cdx.id  } name="q.column.options.suggested">
								<input name="q.column.options.name" data-optoin-id="${ cdx.id  }" value="${ cdx.name }">
								<button>REMOVE</button>		
							</td>
						</c:if>
					</tr>	
				</c:forEach>
				
			</table>
		</div>
		
	</div>


	<script src="https://code.jquery.com/jquery-1.12.4.min.js"
		integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ="
		crossorigin="anonymous"></script>
</body>
</html>