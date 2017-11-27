function checkboxChangeHandler(e) {
	const checkbox = e.target;
	checkbox.value = checkbox.checked;
}

function removeArrayNullElement(ary) {
	return ary.filter((elm)=> !$.isEmptyObject(elm));
}

function submitChange() {
	const questionObj = {};
	$('input, select').each((i, el)=>{
		collectDetail(questionObj, el);
	});
	if(questionObj.tags) 
		questionObj.tags = removeArrayNullElement(questionObj.tags);
	if(questionObj.index && questionObj.index.options)
		questionObj.index.options = removeArrayNullElement(questionObj.index.options);
	if(questionObj.column && questionObj.column.options)
		questionObj.column.options = removeArrayNullElement(questionObj.column.options);
	console.log(JSON.stringify(questionObj, null, 4));
	$.ajax({
		type : 'POST',
		url : '/api/questionUpdate',
		data : JSON.stringify(questionObj),
		contentType:"application/json; charset=utf-8",
		dataType:"json",
		processData:false,
		statusCode : {
			400 : function() {
				alert('bad request error!');
			},
			202 : function(resp) {
				window.location.href = resp.getResponseHeader('Location');
			}
		}
	});
}

function goDetail(id) {
	window.location.href = "/detail/"+id;
}

function goManager() {
	window.location.href = "/";
}

function setObjectPropValueByFormName(obj, name, value) {
	const props = name.split('.');
	const lastIdx = props.length-1;
	const arrayTypeReg = /(.*)\[(\d+)\]$/;
	let cur = obj;
	for(let i=0; i<lastIdx; i++) {
		let propName = props[i];
		const groups = arrayTypeReg.exec(propName);
		if(groups) {
			propName = groups[1];
			let idx  = parseInt(groups[2]);
			if(!cur.hasOwnProperty(propName)) { 
				cur[propName] = new Array(idx+1);
				cur[propName][idx] = {};
			} else { 
				while(idx >= cur[propName].length) cur[propName].push({});
				if(!cur[propName][idx]) cur[propName][idx] = {};
			}
			cur = cur[propName][idx];
		} else {
			if(!cur.hasOwnProperty(propName)) cur[propName] = {}
			cur = cur[propName];
		}
	}
	if(value) cur[props[lastIdx]] = value;
	return cur;
}

function collectDetail(obj, input) {
	const $input = $(input);
	const propName = $input.prop('name');
	let propValue =  $input.val();
	const deepObj = setObjectPropValueByFormName(obj, propName, propValue);
	const dataset = $input.data();
	const idKey = Object.keys(dataset).filter((key)=>key.endsWith('Id'))[0];
	if(dataset[idKey]) deepObj['id'] = dataset[idKey];
}

function addTag(e) {
	const wrapper = $('.question-tag-wrapper');
	const count = wrapper.children().length;
	const tagDiv = $('<div>').addClass('tag input-group col-md-4');
	const addon  = $('<span>').addClass('input-group-addon').text('TAG');
	const input = $('<input>').attr('name', `tags[${count}].name`).addClass('form-control input-sm');
	const btnGroup = $('<div>').addClass('input-group-btn');
	const button = $('<button>').text('REMOVE').on('click', removeTag).addClass('btn btn-sm btn-primary no-radius');
	btnGroup.append(button);
	tagDiv.append(addon).append(input).append(btnGroup);
	wrapper.append(tagDiv);
}

function removeTag(e) {
	$(e.target).parent().parent().remove();
}

function captitalize(str) {
	return str.replace(/^[a-z]/, (c)=>c.toUpperCase());
}

function addOptionValue(type) {
	const optionValues = $('.'+type).find('.option-values');
	const count = optionValues.children().length;
	const optionValue = $('<div>').addClass('option-value col-md-6 input-group');
	const id = `checkbox-${type}-${count+1}`;
	const addon = $('<span>').addClass('input-group-addon');
	const checkbox = $('<input>').attr({
		type : 'checkbox',
		id : id,
		name : `${type}.options[${count}].suggested`,
		'data-toggle' : "tooltip",
		'data-placement' : "left",
		'data-original-title' : "is right answer?"
	}).val('false').on('change', checkboxChangeHandler);
	addon.append(checkbox);
	const input = $('<input>').attr('name', `${type}.options[${count}].name`)
							  .addClass('form-control input-sm');
	const btnGroup = $('<div>').addClass('input-group-btn');
	const button = $('<button>').text('REMOVE').on('click', removeOptionValue)
								.addClass('btn btn-sm btn-primary no-radius');
	btnGroup.append(button);
	optionValue.append(addon).append(input).append(btnGroup);
	optionValues.append(optionValue);
	$('[data-toggle="tooltip"]').tooltip();
}

function addOptionGroup(type, e) {
	if($('.'+type).length) {
		//option group exists
		addOptionValue(type);
		return;
	}
	//not exists
	const optionGroups = $('.option-groups');
	const group = $('<div>').addClass('group').addClass(type);
	const groupName = $('<div>').addClass('group-name input-group col-md-6');
	const content = captitalize(type)+" Name";
	const addon = $('<span>').addClass('input-group-addon').text(content);
	const input = $('<input>').attr('name', `${type}.name`).addClass('form-control input-sm');
	const btnGroup = $('<div>').addClass('input-group-btn')
							   .append($('<button>')
									   .addClass('btn btn-sm btn-primary no-radius')
									   .text("REMOVE")
									   .on('click', removeGroup));
	groupName.append(addon)
			 .append(input)
			 .append(btnGroup);
	const optoinValues = $('<div>').addClass('option-values');
	group.append(groupName).append(optoinValues);
	optionGroups.append(group);
	addOptionValue(type);
}

function removeGroup(e) {
	const $target = $(e.target);
	const $group = $target.parent().parent().parent();
	const dataset =  $target.parent().prev().data();
	if(!dataset) {
		$group.remove();
		return;
	}
	const keys = Object.keys(dataset);
	if(keys.filter((key)=>key.toLowerCase().indexOf("index")!==-1)[0]) {
		alert('cant remove existing index of question');
		return;
	}
	const gid = dataset[keys.filter((key)=>key.endsWith('Id'))[0]];
	const qid = $('input[data-question-id]').data('questionId');
	if(!gid || !qid) {
		$group.remove();
		return;
	}
	$.ajax({
		url : '/api/removeOptionGroup',
		type : 'DELETE',
		data : $.param({ "qid" : qid, "gid" : gid }),
		contentType : 'application/x-www-form-urlencoded',
		statusCode : {
			400 : function(resp) {
				alert("bad request error!");
			},
			202 : function() {
				$group.remove();
			}
		}
	});
		
}

function removeOptionValue(e) {
	const $target = $(e.target);
	//option value
	const optionValueDiv = $target.parent().parent();
	const opid = optionValueDiv.find('[data-option-id]').data('optionId');
	const gid = optionValueDiv.parent().data('parentId'); 
	if(!opid || !gid) {
		optionValueDiv.remove();
		return;
	}
	$.ajax({
		url : '/api/removeOptionValue',
		type : 'DELETE',
		data : $.param({ "opid" : opid, "gid" : gid }),
		contentType : 'application/x-www-form-urlencoded',
		statusCode : {
			400 : function(resp) {
				alert("bad request error!");
			},
			202 : function() {
				optionValueDiv.remove();
			}
		}
	});
	
}

function doSearch(el) {
	const term = $('[name="search"]').val();
	window.location.href = `/?term=${term}`;
}

function doThis(el) {
	const $el = $(el);
	const $qb = $el.parent().parent();
	const ds = $qb[0].dataset;
	const cmd = $el.text();
	let dataObj = {};
	switch(cmd) {
	case 'Submit':
		dataObj.question = { 'id' : ds.questionId };
		dataObj.answers = [];
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
//		console.log(data);
		$.ajax({
			url: '/api/userAnswer',
			type : 'POST',
			data : JSON.stringify(dataObj),
			contentType:"application/json; charset=utf-8",
			dataType:"json",
			processData:false,
			statusCode : {
				400 : function() {
					alert('bad request error!');
				},
				202 : function(resp) {
					console.log(resp);
					alert('cool, ur answer has submitted sucessfully!');
				}
			}
		});
		break;
	case 'Delete':
		dataObj = { 'id' : ds.questionId };
		$.ajax({
			url: '/api/deleteQuestion',
			type : 'DELETE',
			data : JSON.stringify(dataObj),
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
$(document).ready(function(){
	$('[type="checkbox"]').on('change', checkboxChangeHandler);	
	const $tooltip = $('[data-toggle="tooltip"]');
	if($tooltip.length) {
		$tooltip.tooltip();
	}
	$('[name="search"]').on('keypress', function(e){
		if(e.keyCode === 13) doSearch(e.target);
	}).focus();
});

