<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Question Simple Manager</title>
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
	<h1 style="margin: 15px auto; width: 85%; text-align: center;">Question
		Manager</h1>
	<hr />
	<div class="container-fluid">
		<div class="col-md-8 col-md-offset-2" id="main">
			<div class="input-group col-md-4" style="margin-bottom: 10px;">
				<span class="input-group-btn">
					<button class="btn btn-primary" onclick="goDetail(0)">AddNew</button>
					<button class="btn btn-default" type="button"
						onclick="doSearch(this)">Go</button>
				</span>
				<input type="text" class="form-control" name="search" placeholder="Search for questions...">
			</div>
			<c:set var="qcount" value="0" scope="page"></c:set>
			<c:forEach items="${questions}" var="q">
				<c:set var="qcount" value="${ qcount + 1 }" scope="page"></c:set>
				<div class="card question-block-wrapper"
					data-question-id="${ q.id }" data-question-type="${ q.type }">
					<div class="command-pane">
						<button class="btn btn-sm btn-primary no-radius"
							onclick="goDetail(${ q.id })">Edit</button>
						<button class="btn btn-sm btn-danger  no-radius"
							onclick="doThis(this)">Delete</button>
						<button class="btn btn-sm btn-success no-radius"
							onclick="doThis(this)">Submit</button>
					</div>
					<div class="card-block text-nowrap question-block">
						<div class="question-title">
							<h4>${ qcount }.${ q.title }</h4>
						</div>
						<div class="question-tag-wrapper">
							<c:if test="${ not empty q.tags }">
								<span style="font-weight: bold;">TAGS:</span>
								<c:forEach items="${ q.tags }" var="tag">
									<span class="badge tag" data-tag-id="${ tag.id }">${ tag.name }</span>
								</c:forEach>
							</c:if>
						</div>
						<div class="option-group">
							<table class="table table-sm">
								<thead>
									<c:if test="${ q.index.name != null }">
										<tr>
											<th class="col-md-2">${ q.index.name }</th>
											<c:if test="${ q.column != null && q.column.name != null}">
												<th class="col-md-2">${ q.column.name }</th>
											</c:if>
										</tr>
									</c:if>
								</thead>
								<tbody>
									<c:forEach items="${ q.index.options }" var="row">
										<tr>
											<td class="col-md-2">
												<c:choose>
													<c:when test="${ q.column == null }">
														<input id="option-${ row.id }" data-option-id="${ row.id }" type="checkbox" />
														<label for="option-${ row.id }">${ row.name }</label>
													</c:when>
													<c:otherwise>
													${ row.name }
												</c:otherwise>
												</c:choose>
											</td>
											<c:if test="${ not empty q.column }">
											<c:forEach items="${ q.column.options }" var="col">
												<td class="col-md-2">
													<input id="option-${ row.id }-${ col.id }" data-option-id="${ row.id }-${ col.id }" type="checkbox" />
													<label for="option-${ row.id }-${ col.id }">${ col.name }</label>
												</td>
											</c:forEach>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</c:forEach>


		</div>
	</div>
	<script src="https://code.jquery.com/jquery-1.12.4.min.js"
		integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ="
		crossorigin="anonymous"></script>

	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
		integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
		crossorigin="anonymous"></script>

	<script type="text/javascript" src="/js/app.js"></script>
</body>
</html>