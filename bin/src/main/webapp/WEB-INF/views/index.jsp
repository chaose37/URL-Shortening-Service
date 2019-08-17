<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Kakao Pay 사전과제</title>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
<script>
	//버튼클릭시 이벤트
	var domain = "http://localhost:8080";
	
	function clickBtn ()
	{
		var url = $("#S_URL").val();
		url = $.trim(url);
		if(!url)
		{
			alert("URL을 입력해 주세요.");
			return;
		}
// 		if(!checkDetailUrl(url))
// 		{
// 			if(!confirm("올바르지 않은 URL 값을 입력하셨습니다.\n계속 실행하겠습니까?.")){
//   			  $("#S_URL").val("");
//   	    	  $("#S_RESULT").val("");
// 			  return;
// 			};
// 		};
		$.ajax({
	        type:"POST",
	        url:"/",
	        data: {"url":url} 
	    })
	    .done(function(result) { 
	    	$("#S_RESULT").val(domain + "/" + result);
		}) 
		.fail(function(error) { 
			alert(error);
		}) 
	    ;
	}
	
// 	//URL에 대한 유효성을 체크한다.
// 	function checkDetailUrl(strUrl) {
// 	    var expUrl = /^(https?):\/\/([^:\/\s]+)(:([^\/]*))?((\/[^\s/\/]+)*)?\/?([^#\s\?]*)(\?([^#\s]*))?(#(\w*))?$/;
// 	    return expUrl.test(strUrl);
// 	}


</script>
</head>
<body>
	<div class="container">
		<div class="row"> 
			<div class="col-md-12"> 
				<div class="page-header"> 
					<h1>카카오페이 사전과제</h1> 
					<h2>단축 URL 만들기</h2> 
				</div> 
				<form class="form-horizontal"> 
				  <div class="form-group">
				    <label for="S_URL" class="col-sm-2 control-label">Input : </label>
				    <div class="col-sm-9">
				      <input type="text" class="form-control" id="S_URL" placeholder="Input URL">
				    </div>
				    <div class="col-sm-1" style="padding-left:0;">
					  <input type="button" class="btn" value="실행" style="max-width: 65px" onclick="clickBtn()"> 
				    </div>
				  </div>
				  <div class="form-group">
				    <label for="S_URL" class="col-sm-2 control-label">Output : </label>
				    <div class="col-sm-10">
				      <input type="text" class="form-control" id="S_RESULT" placeholder="결과가 출력됩니다." readonly>
				    </div>
				  </div>
				</form> 
			</div> 
		</div> 
	</div>
</body>
</html>