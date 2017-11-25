function removeArrayNullElement(ary) {
	return ary.filter((elm)=>elm != undefined && elm!=null);
}

function submitChange() {
	const questionObj = {};
	$('input, select').each((i, el)=>{
		collectDetail(questionObj, el);
	});
	if(questionObj.tags) 
		questionObj.tags = removeArrayNullElement(questionObj.tags);
	if(questionObj.index.options)
		questionObj.index.options = removeArrayNullElement(questionObj.index.options);
	if(questionObj.column && questionObj.column.options)
		questionObj.column.options = removeArrayNullElement(questionObj.column.options);
	console.log(JSON.stringify(questionObj, null, 4));
}

function goDetail(id) {
	window.location.href = "/detail/"+id;
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
	console.log(propValue);
	switch(propValue) {
		case "on":  propValue = true;  break;
		case "off": propValue = false; break;
	}
	const deepObj = setObjectPropValueByFormName(obj, propName, propValue);
	const dataset = $input.data();
	const idKey = Object.keys(dataset).filter((key)=>key.endsWith('Id'))[0];
	if(dataset[idKey]) deepObj['id'] = dataset[idKey];
}

function addTag(e) {
	const wrapper = $('.question-tag-wrapper');
	const count = wrapper.children().length;
	const tagDiv = $('<div>').addClass('tag');
	const input = $('<input>').attr('name', `tags[${count}].name`);
	const button = $('<button>').text('REMOVE').on('click', removeTag);
	tagDiv.append(input).append(button);
	wrapper.append(tagDiv);
}

function removeTag(e) {
	$(e.target).parent().remove();
}

function captitalize(str) {
	return str.replace(/^[a-z]/, (c)=>c.toUpperCase());
}

function addOptionValue(type) {
	const optionValues = $('.'+type).find('.option-values');
	const count = optionValues.children().length;
	const optionValue = $('<div>').addClass('option-value');
	const id = `checkbox-${type}-${count+1}`;
	optionValue.append($('<label>').attr('for', id).text('Right Answer'))
			   .append($('<input>').attr('id', id).attr('type', 'checkbox').attr('name', `${type}.options[${count}].suggested`))
			   .append($('<input>').attr('name', `${type}.options[${count}].name`))
			   .append($('<button>').text('REMOVE').on('click', removeOptionValue));
	optionValues.append(optionValue);
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
	const groupName = $('<div>').addClass('group-name');
	const content = captitalize(type)+" Name";
	groupName.append($('<label>').text(content))
			 .append($('<input>').attr('name', 'index.name'))
			 .append($('<button>').text("REMOVE").on('click', removeGroup));
	const optoinValues = $('<div>').addClass('option-values');
	group.append(groupName).append(optoinValues);
	optionGroups.append(group);
	addOptionValue(type);
}

function removeGroup(e) {
	$(e.target).parent().parent().remove();
}

function removeOptionValue(e) {
	$(e.target).parent().remove();
}

function doThis(el) {
	const $el = $(el);
	const $qb = $el.parent().parent();
	const ds = $qb[0].dataset;
	const cmd = $el.text();
	const dataObj = {};
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
		const data = JSON.stringify(dataObj);
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
		const data = JSON.stringify(dataObj);
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
$(document).ready(function(){
	$('[type="checkbox"]').on('change', function(e){
		const checkbox = e.target;
		checkbox.value = checkbox.checked;
	});	
});

