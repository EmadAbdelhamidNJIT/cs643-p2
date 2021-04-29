Code: 
https://github.com/EmadAbdelhamidNJIT/cs643-p2 

The code is also running on a cloud9 environment at;
Arn:aws:cloud9:us-east-1:125764523568:environment:f445498c7bbd4ec4ade39bf0aaf4cee2 

The application determines the accuracy of the prediction 
The Model Accuracy on Test Data: 0.58125

ERM
After creating and ERM cluster for spark and choosing spark and hadoop software, log into the master instance through
Start PuTTY.
In the Category list, click Session.
In the Host Name field, type hadoop@ec2-54-236-62-118.compute-1.amazonaws.com
In the Category list, expand Connection > SSH, and then click Auth.
For Private key file for authentication, click Browse and select the private key file (mykeypair.ppk) used to launch the cluster.
Click Open.
Click Yes to dismiss the security alert.

In the ERM cluster add a step to execute the following command

spark-submit --deploy-mode cluster --executor-memory 18G --executor-cores 4 s3://aws-logs-125764523568-us-east-1/cs643-p2-artifact-0.0.1-SNAPSHOT-jar-with-dependencies.jar --class SparkML.java

After SSH into the master server 
The training file and the validation file have to been copied from the s3 bucket
"s3://aws-logs-125764523568-us-east-1/data/TrainingDataset.csv";
"s3://aws-logs-125764523568-us-east-1/data/ValidationDataset.csv"

Using the following commands:
aws s3 cp s3://aws-logs-125764523568-us-east-1/data/TrainingDataset.csv .
aws s3 cp s3://aws-logs-125764523568-us-east-1/data/ValidationDataset.csv .


Docker:
To build a docker image, from the home directory run docker build -t demo/app
To run the docker build, docker run -p 8080:8080 demo/app


		


