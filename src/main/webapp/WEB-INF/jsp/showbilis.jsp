<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>哔哩哔哩</title>
</head>
<body>
	<table border=1>
		<thead>
			<tr>
				<th>视频标题</th>
				<th>视频编号</th>
				<th>弹幕编号</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="list" items="${list}">
				<tr>
					<th>${list.title}</th>
					<th><code href="http://www.bilibili.com/video/av${list.aid}">${list.aid}</code></th>
					<th><code href="http://comment.bilibili.com/${list.cid}.xml">${list.cid}</code></th>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>