
#!/bin/bash

# JSON object to pass to Lambda Function
#json={"\"name\"":"\"Fred\u0020Smith\",\"param1\"":1,\"param2\"":2,\"param3\"":3}
#json={"\"name\"":"\"\",\"calcs\"":2500000,\"sleep\"":0,\"loops\"":25}

fileops=$1
optype=$2
numfiles=$3
numfileops=$4

#numfiles=3000
#fileops=W
#numfileops=2200
#optype=B


callservice(){
	json={"\"name\"":"\"\",\"numfiles\"":$numfiles,\"fileops\"":"\"$fileops\",\"numfileops\"":$numfileops,\"optype\"":"\"$optype\",\"nodelete\"":"\"false\""}

	#echo $json
	##echo "Invoking Lambda function using API Gateway"
	#time output=`curl -s -H "Content-Type: application/json" -X POST -d  $json https://4k8a0ku5g5.execute-api.us-east-1.amazonaws.com/calcs_test`
	##time output=`curl -s -H "Content-Type: application/json" -X POST -d  $json {INSERT API GATEWAY URL HERE}`

	##echo ""
	#echo "CURL RESULT:"
	#echo $output
	##echo ""
	##echo ""
	#exit

	echo "Invoking Lambda function using AWS CLI"
	time output=`aws lambda invoke --invocation-type RequestResponse --function-name calcs --region us-east-1 --payload $json /dev/stdout | head -n 1 | head -c -2 ; echo`
	#aws lambda invoke --invocation-type RequestResponse --function-name logtest --region us-east-1 --payload $json /dev/stdout 
	echo "AWS CLI RESULT:"
	echo $output
	echo ""
}

loop(){
	echo "fileops="$fileops", optype="$optype", numfiles="$numfiles", numfileops"=$numfileops
	number=3
	for (( i=1 ; i <= $number; i++ ))
	do
	echo "Round: " $i
		callservice 
	done
}

loop | tee -a ~/output.txt


