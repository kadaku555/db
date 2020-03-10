var paginator = angular.module('paginator', ['myDebounce', 'keypress']);
paginator.directive('paginator', function($debounce, filterFilter, orderByFilter) {
	var pageSizeLabel = "Page size";
	return {
		priority : 0,
		restrict : 'ACE',
		scope : {
			remote: '=',
			items : '=',
			filter : '=',
			actionafterfilter : '=',//function
			actionafterorder : '=',//function
			actionafterdisplay : '=',//function
			actionafterchangepage : '='//function
		},
		template : '<div style="float: left;"><button class="btn btn-default btn-sm" ng-disabled="isFirstPage()" ng-click="firstPage()"><span class="glyphicon glyphicon-step-backward"></span></button>'
				+ '<button class="btn btn-default btn-sm" ng-disabled="isFirstPage()" ng-click="decPage()"><span class="glyphicon glyphicon-chevron-left"></span></button>'
				+ '<span style="margin:5px;"><input class="btn btn-sm" ng-model="displayPage" title="press enter to change page" ng-blur="correctPage()" ui-keyup="{enter:\'gotoPage()\', esc:\'correctPage()\'}" size="{{numberOfPages().toString().length}}" maxlength="{{numberOfPages().toString().length}}" style="text-align:right" />/{{numberOfPages()}}</span>'
				+ '<button class="btn btn-default btn-sm" ng-disabled="isLastPage()" ng-click="incPage()"><span class="glyphicon glyphicon-chevron-right"></span></button>'
				+ '<button class="btn btn-default btn-sm" ng-disabled="isLastPage()" ng-click="lastPage()"><span class="glyphicon glyphicon-step-forward"></span></button>'
				+ '<span style="margin:5px;">'
				+ pageSizeLabel
				+ '</span>'
				+ '<select class="btn btn-sm" ng-model="paginator.pageSize" ng-change="firstPage()" ng-options="size for size in pageSizeList"></select>'
				+ '<span style="margin:5px;">number of rows : {{paginator.rowNumber}}</span></div>'
				+ '<div ng-transclude style="margin-top: 5px;"></div>'
				+ '<div style="float: left;"><button class="btn btn-default btn-sm" ng-disabled="isFirstPage()" ng-click="firstPage()"><span class="glyphicon glyphicon-step-backward"></span></button>'
				+ '<button class="btn btn-default btn-sm" ng-disabled="isFirstPage()" ng-click="decPage()"><span class="glyphicon glyphicon-chevron-left"></span></button>'
				+ '<span style="margin:5px;"><input class="btn btn-sm" ng-model="displayPage" title="press enter to change page" ng-blur="correctPage()" ui-keyup="{enter:\'gotoPage()\', esc:\'correctPage()\'}" size="{{numberOfPages().toString().length}}" maxlength="{{numberOfPages().toString().length}}" style="text-align:right" />/{{numberOfPages()}}</span>'
				+ '<button class="btn btn-default btn-sm" ng-disabled="isLastPage()" ng-click="incPage()"><span class="glyphicon glyphicon-chevron-right"></span></button>'
				+ '<button class="btn btn-default btn-sm" ng-disabled="isLastPage()" ng-click="lastPage()"><span class="glyphicon glyphicon-step-forward"></span></button>'
				+ '<span style="margin:5px;">'
				+ pageSizeLabel
				+ '</span>'
				+ '<select class="btn btn-sm" ng-model="paginator.pageSize" ng-change="firstPage()" ng-options="size for size in pageSizeList"></select>'
				+ '<span style="margin:5px;">number of rows : {{paginator.rowNumber}}</span></div>',
		replace : false,
		transclude : true,
		compile : function compile(tElement, tAttrs, transclude) {
			return {
				pre : function preLink(scope, iElement, iAttrs,	controller) {
					var forceSortRows = false;
					var forceFilterRows = false;
					var forceDisplayRows = false;
					var filterBypass = null;
					var filter = scope.filter || [{filter: "$", value: "", custom: true, strict: false}];
					
//		in html			 pageupd="{{paginator.$pageRender}}"

					iAttrs.$observe("pageupd", function(val){
						if(val > 2){
							scope.$evalAsync(function(){
								//console.log("totoo"+scope.remote.$pageRender);
							});
						}
					});
					
					//default callbacks
					scope.callbacks = {actionafterfilter: angular.noop, 
							actionafterorder: angular.noop, 
							actionafterdisplay: angular.noop,
							actionafterchangepage: angular.noop,
							actionafterpagesize: angular.noop,
							actionafterinit: angular.noop};
					
					//check if remote provided
					if(!angular.isObject(scope.remote)){
						console.warn("Remote must be an object.");
						scope.remote = {};
					};
					scope.remote.$pageRender = 0;
					//copy provided callbacks into inner callbacks object
					angular.extend(scope.callbacks, scope.remote);
					
					//Deprecated usage, copy provided callbacks into inner callbacks object for backward compatibility
					if(scope.actionafterfilter){
						console.warn("Use of directive attribute is deprecated, provide actionafterfilter callback in Remote.");
						scope.callbacks.actionafterfilter = scope.actionafterfilter;
					};
					if(scope.actionafterorder){
						console.warn("Use of directive attribute is deprecated, provide actionafterorder callback in Remote.");
						scope.callbacks.actionafterorder = scope.actionafterorder;
					};
					if(scope.actionafterdisplay){
						console.warn("Use of directive attribute is deprecated, provide actionafterdisplay callback in Remote.");
						scope.callbacks.actionafterdisplay = scope.actionafterdisplay;
					};
					if(scope.actionafterchangepage){
						console.warn("Use of directive attribute is deprecated, provide actionafterchangepage callback in Remote.");
						scope.callbacks.actionafterchangepage = scope.actionafterchangepage;
					};
					
					scope.pageSizeList = [ 10, 20, 30, 50, 100, 200, 300 ];
					scope.paginator = {
						rowNumber: 0,
						currentPage : 0
					};
					if(!scope.paginator.pageSize){
						scope.paginator.pageSize = 30;
					}
					
					scope.displayPage = 1;
					scope.predicate = '';
					scope.filteredRows = scope.items;
					scope.sortedRows = scope.filteredRows;
					scope.displayedRows = scope.sortedRows;

					scope.isFirstPage = function() {
						return scope.paginator.currentPage == 0;
					};
					
					scope.isLastPage = function() {
						return scope.paginator.currentPage >= scope.sortedRows.length / scope.paginator.pageSize - 1;
					};
					
					scope.incPage = function() {
						if (!scope.isLastPage()) {
							scope.displayPage++;
							scope.gotoPage();
						}
					};

					scope.decPage = function() {
						if (!scope.isFirstPage()) {
							scope.displayPage--;
							scope.gotoPage();
						}
					};

					scope.firstPage = function() {
						scope.displayPage = 1;
						scope.gotoPage();
					};
					
					scope.lastPage = function() {
						scope.displayPage = scope.numberOfPages();
						scope.gotoPage();
					};

					scope.gotoPage = function(){
					    var page = scope.displayPage;
					    if(scope.numberOfPages() > 0 && (page <= 0 || page > scope.numberOfPages())){
					        alert("Please set the page between 1 and " + scope.numberOfPages());
					        return;
					    }
					    //console.log("goto page "+page);
					    scope.paginator.currentPage = page - 1;
					    scope.callbacks.actionafterchangepage(scope.paginator.currentPage);//callback for page
					    scope.remote.setDisplayedRows();
					};
					
					scope.correctPage = function(){
						scope.displayPage = scope.paginator.currentPage + 1;
					};

					scope.numberOfPages = function() {
						var nbOfPages = Math.ceil(scope.sortedRows.length / scope.paginator.pageSize);
						return nbOfPages;
					};

					//Deprecated method
					scope.$watch("filter", function(newValue, oldValue) {
						if(newValue !== oldValue){
							console.warn("Deprecated method, use remote setFilter instead.");
							filter = newValue;
							forceFilterRows = false;
							filterBypass = null;
							$debounce(scope.remote.applyQuery,	100);
						}
					}, true);
					
					scope.$watch('paginator.pageSize', function(newValue, oldValue){
						if(newValue == oldValue){
							return;
						}	
						scope.callbacks.actionafterpagesize(newValue);
						//TODO: to remove, kept for backward compatibility
						scope.$emit('pageSizeUpdated');					
					});

					// ---- Functions available in parent scope ----

					scope.remote.setFilter = function(data){
						filter = data;
						forceFilterRows = false;
						filterBypass = null;
						scope.remote.applyQuery();
						scope.firstPage();
					};
					
					scope.remote.getPredicate = function(){
						return scope.predicate;
					};
					
					scope.remote.setPredicate = function(val){
						scope.predicate = val;
						scope.remote.sortRows();
					};
					
//					scope.remote.removeFilterOn = function(attr){
//						var attrs = attr.split(".");
//						var tmp = filter;
//						for (var i in attrs){
//							if(i == attrs.length-1){
//								if(tmp!=null){
//									tmp[attrs[i]] = "";
//								}
//							}
//							else{
//								tmp = tmp[attrs[i]];
//							}
//						}
//					};
					
					scope.remote.setPage = function(i){
						scope.displayPage = i;
						scope.gotoPage();
					};
					
					scope.remote.setPageSize = function(i){
						scope.paginator.pageSize = i;
					};
					
					scope.remote.getPageSize = function(){
						return scope.paginator.pageSize;
					};
					
					scope.remote.getCurrentPage = function(){
						return scope.paginator.currentPage;
					};
					
					scope.remote.firstPage = function() {
						scope.firstPage();
					};
					
					scope.remote.decPage = function() {
						scope.decPage();
					};
					
					scope.remote.incPage = function() {
						scope.incPage();
					};
					
					scope.remote.lastPage = function() {
						scope.lastPage();
					};
					
					scope.remote.applyQuery = function(rows) {
						//scope.$parent.incBusy();
						if(filterBypass){
							filterBypass[0](filterBypass[1]);
							//scope.$parent.decBusy();
						}
						else{
							if(rows){
								scope.items = rows;
							}
							//
							//filter = [{filter:..., value:..., strict: true/false}, ...]
							scope.filteredRows = scope.items;
							
							angular.forEach(filter, function(f){
								var search = {};
								if(f.filter.length > 0){
									var types = f.filter.split(".");
									var tmp = search;
									angular.forEach(types, function(type){
										if(tmp[type] == null){
											tmp[type] = {};
										}
										if(types.indexOf(type) == types.length - 1){
							    			tmp[type] = f.value;
							    		}
										tmp = tmp[type];
									});
								}
								scope.filteredRows = filterFilter(scope.filteredRows, search, f.strict);
							});
							//
//							scope.filteredRows = filterFilter(scope.items, filter);
//							//DOCMAINT-4491
//							angular.forEach(filter, function(v, k){
//								if(k!="$"){
//									if(v.length == 0){
//										var tmpRows = [];
//										angular.forEach(scope.filteredRows, function(row){
//											if(angular.isString(row[k]) && (row[k].length == 0 || row[k].indexOf("  ") >= 0)){//very ugly
//												tmpRows.push(row);
//											}
//										});
//										scope.filteredRows = tmpRows;
//									}
//								}
//							});
							scope.callbacks.actionafterfilter(scope.filteredRows);//callback for filter
							if(scope.filteredRows.length < (scope.paginator.currentPage * scope.paginator.pageSize)){
								scope.firstPage();
							};
							scope.remote.sortRows();
							//scope.$parent.decBusy();
						}
					};
					
					scope.remote.sortRows = function() {
						if(forceSortRows || forceDisplayRows){
							forceSortRows = false;
							forceDisplayRows = false;
							scope.remote.applyQuery();
						}
						else{
							//scope.$parent.incBusy();
							scope.sortedRows = orderByFilter(scope.filteredRows, scope.predicate);
							scope.paginator.rowNumber = scope.sortedRows.length;
							scope.callbacks.actionafterorder(scope.sortedRows);//callback for sort
							scope.remote.setDisplayedRows();
							//scope.$parent.decBusy();
						}
					};
					
					scope.remote.setDisplayedRows = function() {
						if(forceDisplayRows){
							forceDisplayRows = false;
							scope.remote.applyQuery();
						}
						else{
							//scope.$parent.incBusy();
							var start = scope.paginator.currentPage * scope.paginator.pageSize;
							var limit = scope.paginator.pageSize;
							scope.displayedRows = scope.sortedRows.slice(start, start + limit);
							scope.callbacks.actionafterdisplay(scope.displayedRows);//callback for page
							//scope.$parent.decBusy();
						}
						scope.remote.$pageRender++;
					};
					
					scope.remote.directSetFilteredRows = function(list, reApplyCallback, data){
						//scope.$parent.incBusy();
						forceFilterRows = true;
						filterBypass = [reApplyCallback, data];
						scope.filteredRows = list;
						scope.callbacks.actionafterfilter(scope.filteredRows);//callback for filter
						if(scope.filteredRows.length < scope.items.length){
							scope.firstPage();
						};
						scope.remote.sortRows();
						//scope.$parent.decBusy();
					};
					
					scope.remote.directSetSortedRows = function(list){
						//scope.$parent.incBusy();
						forceSortRows = true;
						scope.sortedRows = list;
						scope.paginator.rowNumber = scope.sortedRows.length;
						scope.callbacks.actionafterorder(scope.sortedRows);//callback for sort
						scope.remote.setDisplayedRows();
						//scope.$parent.decBusy();
					};
					
					scope.remote.directSetDisplayedRows = function(list){
						//scope.$parent.incBusy();
						forceDisplayRows = true;
						scope.displayedRows = list;
						scope.callbacks.actionafterdisplay(scope.displayedRows);//callback for page
						//scope.$parent.decBusy();
					};
					
					scope.remote.getFilteredRows = function() {
						return scope.filteredRows;
					};
					
					scope.remote.getSortedRows = function(){
						return scope.sortedRows;
					};
					
					scope.remote.getDisplayedRows = function() {
						return scope.displayedRows;
					};
					
					//Deprecated method
					scope.$parent.setPredicate = function(val){
						console.warn("Deprecated method, use remote setPredicate instead.");
						scope.remote.setPredicate(val);
					};
					
					//Deprecated method
					scope.$parent.setPage = function(i){
						console.warn("Deprecated method, use remote setPage instead.");
						scope.remote.setPage(i);
					};
					
					//Deprecated method
					scope.$parent.setPageSize = function(i){
						console.warn("Deprecated method, use remote setPageSize instead.");
						scope.remote.setPageSize(i);
					};
					
					//Deprecated method
					scope.$parent.getPageSize = function(){
						console.warn("Deprecated method, use remote getPageSize instead.");
						return scope.remote.getPageSize();
					};
					
					//Deprecated method
					scope.$parent.getCurrentPage = function(){
						console.warn("Deprecated method, use remote getCurrentPage instead.");
						return scope.remote.getCurrentPage();
					};
					
					//Deprecated method
					scope.$parent.firstPage = function() {
						console.warn("Deprecated method, use remote firstPage instead.");
						scope.remote.firstPage();
					};
					
					//Deprecated method
					scope.$parent.decPage = function() {
						console.warn("Deprecated method, use remote decPage instead.");
						scope.remote.decPage();
					};
					
					//Deprecated method
					scope.$parent.incPage = function() {
						console.warn("Deprecated method, use remote incPage instead.");
						scope.remote.incPage();
					};
					
					//Deprecated method
					scope.$parent.lastPage = function() {
						console.warn("Deprecated method, use remote lastPage instead.");
						scope.remote.lastPage();
					};

					//Deprecated method
					scope.$parent.applyQuery = function(rows) {
						console.warn("Deprecated method, use remote applyQuery instead.");
						scope.remote.applyQuery(rows);
					};

					//Deprecated method
					scope.$parent.sortRows = function() {
						console.warn("Deprecated method, use remote sortRows instead.");
						scope.remote.sortRows();
					};

					//Deprecated method
					scope.$parent.setDisplayedRows = function() {
						console.warn("Deprecated method, use remote setDisplayedRows instead.");
						scope.remote.setDisplayedRows();
					};
					
					//Deprecated method
					scope.$parent.directSetDisplayedRows = function(list){
						console.warn("Deprecated method, use remote directSetDisplayedRows instead.");
						scope.remote.directSetDisplayedRows(list);
					};

					//Deprecated method
					scope.$parent.getFilteredRows = function() {
						console.warn("Deprecated method, use remote getFilteredRows instead.");
						return scope.remote.getFilteredRows();
					};
					
					//Deprecated method
					scope.$parent.getSortedRows = function(){
						console.warn("Deprecated method, use remote getSortedRows instead.");
						return scope.remote.getSortedRows();
					};

					//Deprecated method
					scope.$parent.getDisplayedRows = function() {
						console.warn("Deprecated method, use remote getDisplayedRows instead.");
						return scope.remote.getDisplayedRows();
					};
				},
				post : function postLink(scope, iElement, iAttrs, controller) {
					scope.remote.applyQuery();
					scope.callbacks.actionafterinit();
					//TODO: remove, keep for backward compatibility
					scope.$emit('PaginatorInitialized');
				}
			};
		}
	};
});


