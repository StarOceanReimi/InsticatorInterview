<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Question Detail</title>
</head>
<body>
	<h1>Question Detail:</h1>
	<hr/>

	<div class="question-block">
		<div class="command-pane">
			<button onclick="submitChange(event)">Submit</button>
		</div>
		<div class="question-title"><label>Question Title: </label><input name="title"  value="${ empty q.title ? '' : q.title }"/></div>
		<div class="question-type">
			<label>Question Type: </label>
			<select name="type">
				<option ${ q.type.ordinal() == 0 ? "selected" : "" } value="trivia">TRIVIA</option>
				<option ${ q.type.ordinal() == 1 ? "selected" : "" } value="poll">POLL</option>
				<option ${ q.type.ordinal() == 2 ? "selected" : "" } value="checkbox">CHECKBOX</option>
				<option ${ q.type.ordinal() == 3 ? "selected" : "" } value="matrix">MATRIX</option>
			</select>
		</div>
		<div class="question-tag-wrapper">
			<div class="command-pane-inner">
				<button onclick="addTag(event)">ADD TAGS</button>
			</div>
			<c:if test="${ not empty q.tags }">
				<c:forEach items="${ q.tags }" var="tag" varStatus="vs">
					<div class="tag">
						<input data-tag-id="${ tag.id }" name="tags[${ vs.index }].name" value="${ tag.name }">
						<button onclick="removeTag(event)">REMOVE</button>
					</div>
				</c:forEach>
			</c:if>
		</div>
		<div class="option-groups">
			<div class="command-pane-inner">
				<button onclick="addOptionGroup('index', event)">ADD INDEX</button>
				<button onclick="addOptionGroup('column', event)">ADD COLUMN</button>
			</div>
			<c:if test="${ not empty q.index }">
				<div class="group index">
					<div class="group-name">
						<label>Index Name</label>
						<input data-index-id="${ q.index.id }" name="index.name" value="${ empty q.index.name ? '' : q.index.name }">
						<button onclick="removeGroup(event)">REMOVE</button>
					</div>
					<div class="option-values">
						<c:set var="iCnt" value="${ empty q.index ? 0 : q.index.options.size() }" scope="page"></c:set>
						<c:set var="iIter" value="${ empty q.index ? null : q.index.options.iterator() }" scope="page"></c:set>
						<c:forEach begin="0" end="${ iCnt }" varStatus="vs">
							<c:if test="${ not empty iIter and iIter.hasNext() }">
								<div class="option-value">
									<c:set var="idx" value="${ iIter.next() }" scope="page"></c:set>
									<label for="checkbox-index-${ vs.count }">Right Answer</label>
									<input id="checkbox-index-${ vs.count }" type="checkbox" 
										   data-optoin-id="${ idx.id  }" 
										   name="index.options[${ vs.index }].suggested"
										   ${ idx.suggested ? "checked value='true'" : "value='false'" } >
									<input name="index.options[${ vs.index }].name" data-optoin-id="${ idx.id  }" value="${ idx.name }">
									<button onclick="removeOptionValue(event)">REMOVE</button>
								</div>				
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:if>
			<c:if test="${ not empty q.column }">
				<div class="group column">
					<div class="group-name">
						<label>Column Name</label>
						<input data-column-id="${ q.column.id }" name="column.name" value="${ empty q.column.name ? '' : q.column.name }">
						<button onclick="removeGroup(event)">REMOVE</button>
					</div>
					<div class="option-values">
						<c:set var="cCnt" value="${ empty q.column ? 0 : q.column.options.size() }" scope="page"></c:set>
						<c:set var="cIter" value="${ empty q.column ? null : q.column.options.iterator() }" scope="page"></c:set>
						<c:forEach begin="0" end="${ cCnt }" varStatus="vs">
							<c:if test="${ not empty cIter and cIter.hasNext() }">
								<div class="option-value">
									<c:set var="idx" value="${ cIter.next() }" scope="page"></c:set>
									<label for="checkbox-column-${ vs.count }">Right Answer</label>
									<input id="checkbox-column-${ vs.count }" type="checkbox" 
										   data-optoin-id="${ idx.id  }" 
										   name="column.options[${ vs.index }].suggested"
										   ${ idx.suggested ? "checked value='true'" : "value='false'" }>
									<input name="column.options[${ vs.index }].name" data-optoin-id="${ idx.id  }" value="${ idx.name }">
									<button onclick="removeOptionValue(event)">REMOVE</button>
								</div>				
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</div>
	</div>


	<script src="https://code.jquery.com/jquery-1.12.4.min.js"
		integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ="
		crossorigin="anonymous"></script>
	<script type="text/javascript" src="/js/app.js"></script>
</body>
</html>