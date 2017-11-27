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
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
	integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
	crossorigin="anonymous">

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
	integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp"
	crossorigin="anonymous">
	
<link rel="stylesheet" href="/css/general.css">
</head>
<body>
	<h1 style="margin: 15px auto; width: 85%; text-align: center;">Question Detail</h1>
	<hr/>

	<div class="container col-md-6 col-md-offset-3 question-block">
		<div class="command-pane">
			<button class="btn btn-success no-radius" onclick="submitChange(event)">Submit</button>
			<button class="btn btn-primary no-radius" onclick="javascript:history.go(-1)">Back</button>
		</div>
		<div class="form-group question-title">
			<label for="titleInput">Question Title</label>
			<input name="title"  
				   id="titleInput"
				   class="form-control"
				   value="${ empty q.title ? '' : q.title }"
				   data-question-id="${ q.id }"/>
		</div>
		<div class="question-type input-group col-md-4">
			<span class="input-group-addon">Question Type</span>
			<select class="form-control" name="type">
				<option ${ q.type.ordinal() == 0 ? "selected" : "" } value="trivia">TRIVIA</option>
				<option ${ q.type.ordinal() == 1 ? "selected" : "" } value="poll">POLL</option>
				<option ${ q.type.ordinal() == 2 ? "selected" : "" } value="checkbox">CHECKBOX</option>
				<option ${ q.type.ordinal() == 3 ? "selected" : "" } value="matrix">MATRIX</option>
			</select>
		</div>
		<div class="question-tag-wrapper">
			<div class="command-pane-inner">
				<button class="btn btn-sm btn-primary no-radius" onclick="addTag(event)">ADD TAGS</button>
			</div>
			<c:if test="${ not empty q.tags }">
				<c:forEach items="${ q.tags }" var="tag" varStatus="vs">
					<div class="tag input-group col-md-4">
						<span class="input-group-addon">TAG</span>
						<input class="form-control input-sm" data-tag-id="${ tag.id }" name="tags[${ vs.index }].name" value="${ tag.name }">
						<div class="input-group-btn">
							<button class="btn btn-sm btn-primary no-radius" onclick="removeTag(event)">REMOVE</button>
						</div>
					</div>
				</c:forEach>
			</c:if>
		</div>
		<div class="option-groups">
			<div class="command-pane-inner">
				<div class="input-group-btn">
				<button class="btn btn-sm btn-primary no-radius" onclick="addOptionGroup('index', event)">ADD INDEX</button>
				<button class="btn btn-sm btn-primary no-radius" onclick="addOptionGroup('column', event)">ADD COLUMN</button>
				</div>
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
					<div class="input-group group-name col-md-6">
						<span class="input-group-addon">
							<c:out value="${ fn:toUpperCase(fn:substring(group.key, 0, 1)) }" />
							<c:out value="${ fn:substring(group.key, 1, fn:length(group.key)) }" /> Name
						</span>
						<input class="form-control input-sm" 
							   data-${ group.key }-id="${ group.value.id }" 
							   name="${ group.key }.name"
							   placeholder="
							   <c:out value="${ fn:toUpperCase(fn:substring(group.key, 0, 1)) }" />
							   <c:out value="${ fn:substring(group.key, 1, fn:length(group.key)) }" /> Name" 
							   value="${ empty group.value.name ? '' : group.value.name }" />
						<div class="input-group-btn">
							<button class="btn btn-sm btn-primary no-radius" onclick="removeGroup(event)">REMOVE</button>
						</div>
					</div>
					<div class="option-values" data-parent-id="${ group.value.id }">
						<c:set var="cnt" value="${ empty group.value ? 0 : group.value.options.size() }" scope="page"></c:set>
						<c:set var="iter" value="${ empty group.value ? null : group.value.options.iterator() }" scope="page"></c:set>
						<c:forEach begin="0" end="${ cnt }" varStatus="vs">
							<c:if test="${ not empty iter and iter.hasNext() }">
								<div class="option-value col-md-6 input-group">
									<c:set var="idx" value="${ iter.next() }" scope="page"></c:set>
									<span class="input-group-addon">
										<input id="checkbox-${ group.key }-${ vs.count }" type="checkbox" 
											   data-toggle="tooltip"
											   data-placement="left"
											   data-original-title="is right answer?"
											   data-optoin-id="${ idx.id  }" 
											   name="${ group.key }.options[${ vs.index }].suggested"
											   ${ idx.suggested ? "checked value='true'" : "value='false'" } >
									</span>
									<input class="form-control input-sm" 
										   name="${ group.key }.options[${ vs.index }].name" 
										   data-option-id="${ idx.id  }" value="${ idx.name }">
									<div class="input-group-btn">
										<button class="btn btn-sm btn-primary no-radius" onclick="removeOptionValue(event)">REMOVE</button>
									</div>
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
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" 
			integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" 
			crossorigin="anonymous"></script>
	<script type="text/javascript" src="/js/app.js"></script>
</body>
</html>