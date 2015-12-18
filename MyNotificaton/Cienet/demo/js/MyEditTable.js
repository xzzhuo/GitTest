// MyEditTable.js

var MyTable = new Object;
MyTable.json_data = new Object;
MyTable.edit = function(obj, url, act)
{
	var td = obj; 
	var item_id=td[0].id; 
	var txt = td.text();

	var input = $("<input type='text' value='" + txt + "'/>"); 

	var w = td.width();
	var h = td.height();

	var new_css = {width: w + "px", height: h + "px", padding: "0", margin: "0"};
	var orig_css = new Object();
	for( var propertyName in new_css ){
		var value = td.css( propertyName );
		orig_css[ propertyName ] = value;
	}
	input.css( new_css );
			
	td.html(input); 
	input.click(function() { return false; });
	input.dblclick(function() { return false; }); 
	input.trigger("focus"); // set the focus

	input.blur(function() { 
		var busy = true;
		var newtxt = $(this).val(); 
		
		if (newtxt != txt) {
			$("#Loading").show();
			
			td.text(newtxt); 
			var caid = $.trim(td.prev().text()); 

			item_name = td.attr("name");

			// do the ajax for sever
			$.post(url, {
				act:act,
				item_id:item_id,
				item_name:item_name,
				item_value:newtxt,
				json_data:MyTable.json_data,
				code:Math.random()
			}, function(data, status) {
				if(process(data,item_id,item_name) == false) 
				{
					location.reload(); 
					td.text(txt);
					alert("invalidate data: "+data);
					return; 
				} 
				td.text(newtxt);
				$("#Loading").hide();
			}); 

		} 
		else 
		{ 
			td.text(newtxt); 
		} 
	});
	
	process = function(data,item_id,item_name)
	{
		MyTable.json_data = data;
		//alert(data);
		data = eval(data);
		$.each(data, function(i, item) {
			//alert(item.nt_id);
		});
		//json_data = data;
		//alert(json_data);
		//alert(data[item_id][item_name]);
		return true;
	}
}

MyTable.checked = function(obj, url, act)
{
	var ckb = obj; 
	var item_id=ckb[0].id;
	val = 0;
	if (ckb.is(':checked') == true)
	{
		val = 1;
	}
	$("#Loading").show();
	item_name = ckb.attr("name");

	// do the ajax for sever
	$.post(url, {
		async:true,
		act:act,
		item_id:item_id,
		item_name:item_name,
		item_value:val,
		code:Math.random()
	}, function(data, status) { 
		if(data != "true") 
		{
			location.reload(); 
			alert("invalidate data: "+data);
			return; 
		}
		$("#Loading").hide();

	});

}

MyTable.show_barrier = function(parent, img_src)
{
	var img = $("<center><table border='0'><tr><td valign='middle'><img src='" + img_src + "' border='0' WIDTH='64' HEIGHT='64'></td></tr></table></center>");
	var div = $("<div style='position:absolute;z-index:1;text-align:center;top:0px;left:0px;background-color:white;' />");
	if (typeof(img_src) != "undefined" && img_src != "")
	{
		div.html(img);
	}

	var document_width = $(document).width();
	var document_height = $(document).height();

	var div_css = {width:document_width,height:document_height,opacity:0.2};
	//var img_css = {position:absolute,top:(document_width - img.width())/2,left:(document_height-img.height())/2,opacity:1};
	//img.css(img_css);
	//alert(img_css);
	div.css(div_css);
	parent.html(div);

	$(window).resize(function() { 
		var document_width = $(document).width();
		var document_height = $(document).height();
		var div_css = {width:document_width,height:document_height,opacity:0.2};
		//var img_css = {position:absolute;z-index:1;top:(document_width - img.width())/2;left:(document_height-img.height())/2;opacity:1};
		//img.css(img_css);
		div.css(div_css);
	}); 

	//parent.center();
	parent.show();
}
	
MyTable.check_all(form)
{
	for (var i=0;i<form.elements.length;i++)
	{
		var e = form.elements[i];
		if (e.name == 'chkall') e.checked = form.chkall.checked;
	}
}