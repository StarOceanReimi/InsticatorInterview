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
		<div class="question-title">
			<label>Question Title: </label>
			<input name="title"  
				   value="${ empty q.title ? '' : q.title }"
				   data-question-id="${ q.id }"/>
		</div>
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
			<jsp:useBean id="groups" class="java.util.LinkedHashMap" scope="page"></jsp:useBean>
			<c:if test="${ not empty q.index }">
				<c:set target="${groups}" property="index" value="${ q.index }"></c:set>
				<c:if test="${ not empty q.column }">
					<c:set target="${ groups }" property="column" value="${ q.column }"></c:set>
				</c:if>
			</c:if>
			<c:forEach var="group" items="${ groups.entrySet() }">
				<div class="group ${ group.key }">
					<div class="group-name">
						<label>
							<c:out value="${ fn:toUpperCase(fn:substring(group.key, 0, 1)) }" />
							<c:out value="${ fn:substring(group.key, 1, fn:length(group.key)) }" />&nbsp;
						Name: </label>
						<input data-${ group.key }-id="${ group.value.id }" name="${ group.key }.name" value="${ empty group.value.name ? '' : group.value.name }">
						<button onclick="removeGroup(event)">REMOVE</button>
					</div>
					<div class="option-values" data-parent-id="${ group.value.id }">
						<c:set var="cnt" value="${ empty group.value ? 0 : group.value.options.size() }" scope="page"></c:set>
						<c:set var="iter" value="${ empty group.value ? null : group.value.options.iterator() }" scope="page"></c:set>
						<c:forEach begin="0" end="${ cnt }" varStatus="vs">
							<c:if test="${ not empty iter and iter.hasNext() }">
								<div class="option-value">
									<c:set var="idx" value="${ iter.next() }" scope="page"></c:set>
									<label for="checkbox-${ group.key }-${ vs.count }">Right Answer</label>
									<input id="checkbox-${ group.key }-${ vs.count }" type="checkbox" 
										   data-optoin-id="${ idx.id  }" 
										   name="${ group.key }.options[${ vs.index }].suggested"
										   ${ idx.suggested ? "checked value='true'" : "value='false'" } >
									<input name="${ group.key }.options[${ vs.index }].name" data-option-id="${ idx.id  }" value="${ idx.name }">
									<button onclick="removeOptionValue(event)">REMOVE</button>
								</div>				
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>


	<script src="https://code.jquery.com/jquery-1.12.4.min.js"
		integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ="
		crossorigin="anonymous"></script>
	<script type="text/javascript" src="/js/app.js"></script>
</body>
</html>