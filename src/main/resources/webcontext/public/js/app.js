function submitChange() {
	
	
}

function doThis(el) {
	var $el = $(el);
	var $qb = $el.parent().parent();
	var ds = $qb[0].dataset;
	var cmd = $el.text();
	var dataObj = {};
	switch(cmd) {
	case 'Submit':
		dataObj.question = { 'id' : ds.questionId };
		dataObj.answers = []
		var answers = $qb.find('[type="checkbox"]:checked').toArray();
		if(ds.questionType == 'MATRIX') {
			answers = answers.map((ans)=>{
				var row_col = ans.dataset.optionId.split('-');
				var rowid = row_col[0], colid = row_col[1];
				return { 'index' : { 'id' : rowid }, 'column' : { 'id' : colid } };
			});
		} else {
			answers = answers.map((ans)=>{ 
				return { 'index' : { 'id' : ans.dataset.optionId } };
			});
		}
		answers.forEach((ans)=>dataObj.answers.push(ans));
		var data = JSON.stringify(dataObj);
		console.log(data);
		$.ajax({
			url: '/api/userAnswer',
			type : 'POST',
			data : data,
			contentType:"application/json; charset=utf-8",
			dataType:"json",
			processData:false,
			statusCode : {
				400 : function() {
					alert('bad request error!');
				},
				202 : function() {
					alert('cool, ur answer has submitted sucessfully!');
				}
			}
		});
		break;
	case 'Delete':
		dataObj = { 'id' : ds.questionId };
		var data = JSON.stringify(dataObj);
		$.ajax({
			url: '/api/deleteQuestion',
			type : 'DELETE',
			data : data,
			contentType:"application/json; charset=utf-8",
			dataType:"json",
			processData:false,
			statusCode : {
				400 : function() {
					alert('bad request error!');
				},
				202 : function(data) {
					window.location.href = data.getResponseHeader('Location');
				}
			}
		});
		break;
	}
}