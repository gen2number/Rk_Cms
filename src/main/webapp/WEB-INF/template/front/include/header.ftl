<div class="container header">
	<div class="span24">
		<div class="topNav clearfix">
			<ul>
				<li class="headerLogin" id="headerLogin" style="display: list-item;">
					<a href="${base}/admin/index">管理界面></a>
					<a href="${base}/user/login/facebook"><img alt="FaceBook登录" class="vm" src="${base}/resources/front/images/facebook.jpg"></a>
					<a href="${base}/user/login/qq"><img alt="QQ登录" class="vm" src="${base}/resources/front/images/qq_login.gif"></a>
					<a href="${base}/user/login/github"><img alt="GitHub登录" class="vm" src="${base}/resources/front/images/github_logo.gif"></a>
				</li>
			</ul>
		</div>
	</div>
	<div class="span24">
		<ul class="mainNav">
			<li><a href="${base}/">首页</a> |</li>
		</ul>
	</div>
	<div class="span24">
		<div class="tagWrap">
			<div class="search">
				<form action="${base}/article/search.rk" id="productSearchForm" method="post">
					<input maxlength="30" value="搜索" class="keyword" name="keyword">
					<button type="submit">搜索</button>
				</form>
			</div>
		</div>
	</div>
</div>