<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>마이페이지</title>
<%-- 외부 CSS 파일 연결하기 --%>
<link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" type="text/css">
<link href="${pageContext.request.contextPath }/resources/css/login.css" rel="stylesheet" type="text/css">
<script src="../js/jquery-3.7.1.js"></script>
<script type="text/javascript">

</script>
</head>
<body>
	<div id="wrapper">
		<header>
			<jsp:include page="../inc/top.jsp"></jsp:include>
		</header>
		
		<jsp:include page="../inc/menu_nav.jsp"></jsp:include>
		
		<section id="content">	
			<h1 id="h01">1:1문의 게시판</h1>
			<hr>
			
			<div id="mypage_nav"> <%-- 사이드 메뉴바 --%>
				<jsp:include page="mypage_menubar.jsp"></jsp:include>
			</div>
			
				
			<!-- 바디부분 시작 -->
			
			<form action="Mypage_OneOnOneList" method="get" name="checkform">
				<div id="my_list">
					<h2>1:1문의</h2>
					<table id="my_table1">
						<tr>
							<th>No.</th>
							<th>제목</th>
							<th>작성자</th>
							<th>등록일</th>
							<th>상세정보</th>
						</tr>
						
						<tr>
							<td>[번호]</td>
							<td>[제목]</td>
							<td>[작성자]</td>
							<td>[등록일]</td>
							<td><input type="button" value="상세정보"></td>
						</tr>
						
						<tr>
							<td>[번호]</td>
							<td>[제목]</td>
							<td>[작성자]</td>
							<td>[등록일]</td>
							<td><input type="button" value="상세정보"></td>
						</tr>
					</table><br>
								
					<div class="pagination">
						<a href="#">&laquo;</a>
						<a href="#">1</a>
						<a class="active" href="#">2</a>
						<a href="#">3</a>
						<a href="#">4</a>
						<a href="#">5</a>
						<a href="#">&raquo;</a>
					</div>
				</div>
							
			</form>
		</section>
	
		<footer>
			<jsp:include page="../inc/bottom.jsp"></jsp:include>
		</footer>
	</div>
	
</body>
</html>