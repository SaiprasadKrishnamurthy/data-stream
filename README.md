
## A Simple Proof Of Solution for a report that has multi-visualization capabilities ##
The intent is to offer a flexible visualization options to the customer for a report. What this means to the customer is they will be able to:
* Plot same type of chart (example PIE) against different data.
* Plot different type of chart (example LINE) against different data.

Both are impossible now in the current framework without having to roll out a new report by the developer(s). This is an incredibly slow and expensive process as it requires a new release of the code.

The intent is to be more flexible with the visualization options so that the customer can give shape to their data themselves.
Often, it is required by the customers to plot charts against different data or different charts against different data.
Currently, we are imposing a serious restriction on the customers by not being able to accomplish this without the help
of engineering.

This is a simple attempt to make the charting a bit more dynamic and interactive for the customers
without having to rely on engineering to roll out a new report.

## A Quick screenshot of how this would appear on the report output screen (UI will be styled later) ##
![Option 1](one.png)
![Option 2](two.png)

This solution is designed within the premise of the current reporting framework. 

The plan is to introduce a new report descriptor called "Multivisual Report" which the report owners can use to defin
the reporting objects. The reports here are applicable to be shaped differently by the framework.

## A Simple Demo ##
Run the following command:

```
mvn clean install -P run
```
Open http://localhost:8999/app/out.html#

The pie chart will have a little button on it's top right to have more visualization options.

This is only implemented for pie currently. But the principle is the same.

The descriptors are defined under src/main/resources/raas (as json files).

There is a REST API provided for easy intergation.

RaasResource.java is the REST API.

## TODO ##
We'll need to have a little SPIKE to integrate and interoperate this library with current PI reporting framework.


## Rough sketch of low level pseudo design ##
```
Service changes:
MultiVisualizationSettings: (new java class)
	- lineX
	- lineY
	- barX
	- barY
	- pieCategory
	- pieCategoryValue
	- pieAggregationFunction (count,sum,avg)
ReportSettings:
	- multiVisualizationSettings: MultiVisualizationSettings (optional)
SimpleReportGenerator::makeQuery(..., ReportSettings rs) {
	.... Last step
	if(rs.multiVisualizationSettings != null) {
		substitutedQuery = MultiVisualizationQueryUtils.generateSQL(rs, substitutedQuery);
	}
}
Adapter changes:
----------------
NEw MEthod just to return the JSON. Don't call Default Report Generator.


Overall Flow:
--------------
ReportsAction (New AJAX Endpoint)  --->  ..... -----> Adapter ------> JSON

Config changes:
----------------
custom_reports.json
	reportName:
	multivisualizationSettings:
```


 
