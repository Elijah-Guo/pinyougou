app.controller("indexController",function($scope,loginService){
	
	$scope.showName = function(){
		loginService.showName().success(function(response){
			//alert(response.userName);
			$scope.loginName = response.userName;
		});
	}
	
});